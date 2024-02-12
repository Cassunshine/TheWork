package cassunshine.thework.alchemy.circle.node;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircleComponent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetItemEvent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetTypeAndRuneEvent;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeType;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.elements.inventory.ElementInventory;
import cassunshine.thework.entities.InteractionPointEntity;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import cassunshine.thework.utils.TheWorkUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class AlchemyNode implements AlchemyCircleComponent {
    public static final Identifier NULL_RUNE = new Identifier(TheWorkMod.ModID, "no_rune");

    /**
     * The ring this node is attached to.
     */
    public final AlchemyRing ring;

    public final int index;

    /**
     * Inventory holding all the elements the node currently has.
     */
    public final ElementInventory inventory = new ElementInventory();

    /**
     * Inventory to move to outputs.
     */
    public final ElementInventory output = new ElementInventory();


    /**
     * The type of this node.
     */
    public AlchemyNodeType nodeType = AlchemyNodeTypes.NONE;

    /**
     * Custom data for this node, stored by the type.
     */
    public AlchemyNodeType.Data typeData = AlchemyNodeType.Data.NONE;

    /**
     * Interaction point entity for nodes that hold items
     */
    public InteractionPointEntity interactionPoint;


    /**
     * The ItemStack this node is holding, if any.
     */
    public ItemStack heldStack = ItemStack.EMPTY;

    public Identifier rune = NULL_RUNE;

    public AlchemyNode(AlchemyRing ring, int index) {
        this.ring = ring;
        this.index = index;
    }

    public float getAngle() {
        return (this.index / (float) ring.nodes.length) * MathHelper.TAU;
    }

    public Vec3d getPosition() {
        return ring.circle.blockEntity.fullPosition.add(MathHelper.sin(getAngle()) * ring.radius, 0, MathHelper.cos(getAngle()) * ring.radius);
    }

    private String[] getBookPages(ItemUsageContext context) {
        var offHand = context.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        var stack = context.getPlayer().getStackInHand(offHand);

        if (stack.isEmpty() || stack.getItem() != Items.WRITABLE_BOOK || !stack.hasNbt()) return null;

        var bookNbt = stack.getNbt();
        var list = bookNbt.getList("pages", NbtElement.STRING_TYPE);

        var ret = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            NbtElement element = list.get(i);
            ret[i] = element.asString().trim();
        }

        return ret;
    }

    public void setTypeAndRune(Identifier type, Identifier rune) {
        var newType = AlchemyNodeTypes.get(type);

        if (newType == nodeType && this.rune == rune) return;

        //Only spawn an interaction entity if the type requires, and there is no rune set.
        if (newType.holdsItems && rune.equals(NULL_RUNE)) {
            if (interactionPoint == null) interactionPoint = ring.circle.blockEntity.addInteractionPoint(getPosition().add(0, ring.circle.blockEntity.getPos().getY() + 1 / 64.0f, 0));
        } else {
            if (interactionPoint != null) interactionPoint = ring.circle.blockEntity.removeInteractionPoint(interactionPoint);
        }

        //Pop off any existing items.
        if (!heldStack.isEmpty() && interactionPoint == null) {
            var pos = getPosition();
            TheWorkUtils.dropItem(ring.circle.blockEntity.getWorld(), heldStack, (float) pos.x, (float) ring.circle.blockEntity.getPos().getY() + 0.5f, (float) pos.z);
        }

        nodeType = newType;
        typeData = newType.getData();

        this.rune = rune;

        ring.circle.regenerateLayouts();
    }

    public boolean isInteractionInRange(ItemUsageContext context) {
        var interactPos = context.getHitPos().withAxis(Direction.Axis.Y, 0);
        var pos = getPosition();

        return interactPos.distanceTo(pos) < 0.7f;
    }

    private TheWorkNetworkEvent swapItemWithPlayer(ItemUsageContext context) {
        var hand = context.getHand();
        var stack = context.getPlayer().getStackInHand(hand);

        //If both items are empty, do nothing.
        if ((stack.isEmpty() && heldStack.isEmpty()))
            return TheWorkNetworkEvents.NONE;

        context.getPlayer().setStackInHand(hand, heldStack);

        return new AlchemyNodeSetItemEvent(stack, this);
    }

    @Override
    public void activate() {
        nodeType.activate(this);

    }

    @Override
    public void activeTick() {
        nodeType.activeTick(this);
    }

    @Override
    public void deactivate() {
        nodeType.deactivate(this);

        inventory.clear();
        output.clear();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("type", nodeType.id.toString());
        nbt.put("typeData", typeData.writeNbt(new NbtCompound()));

        nbt.putString("rune", rune.toString());

        nbt.put("item", heldStack.writeNbt(new NbtCompound()));
        nbt.put("inventory", inventory.writeNbt(new NbtCompound()));

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        nodeType = AlchemyNodeTypes.get(new Identifier(nbt.getString("type")));
        typeData = nodeType.getData();
        typeData.readNbt(nbt.getCompound("typeData"));

        rune = new Identifier(nbt.getString("rune"));

        heldStack = ItemStack.fromNbt(nbt.getCompound("item"));
        inventory.readNbt(nbt.getCompound("inventory"));
    }

    @Override
    public TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context) {
        if (!isInteractionInRange(context)) return TheWorkNetworkEvents.NONE;

        //Get all pages in held book.
        var pages = getBookPages(context);
        if (pages == null || pages.length == 0) return TheWorkNetworkEvents.NONE;

        //Try to parse identifiers from pages.
        ArrayList<Identifier> identifiers = new ArrayList<>();
        for (var page : pages) {
            var id = Identifier.tryParse(page);

            if (id.getPath().isEmpty()) continue;

            if (id != null) identifiers.add(id);

            if (identifiers.size() == 2) break;
        }


        if (identifiers.isEmpty()) return TheWorkNetworkEvents.NONE;

        Identifier[] ids = new Identifier[2];


        if (identifiers.size() == 1) {
            ids[0] = identifiers.get(0);
            ids[1] = NULL_RUNE;
        } else {
            ids[0] = identifiers.get(0);
            ids[1] = identifiers.get(1);
        }

        if (ids[0].equals(nodeType.id) && ids[1].equals(rune)) return new AlchemyNodeSetTypeAndRuneEvent(AlchemyNodeTypes.NONE.id, NULL_RUNE, this);

        return new AlchemyNodeSetTypeAndRuneEvent(ids[0], ids[1], this);
    }

    @Override
    public TheWorkNetworkEvent generateInteractEvent(ItemUsageContext context) {
        if (!isInteractionInRange(context)) return TheWorkNetworkEvents.NONE;

        //If the type holds items, swap item with player item.
        if (nodeType.holdsItems)
            return swapItemWithPlayer(context);

        return TheWorkNetworkEvents.NONE;
    }

    @Override
    public void regenerateInteractionPoints(AlchemyCircleBlockEntity blockEntity) {
        if (nodeType.holdsItems) interactionPoint = blockEntity.addInteractionPoint(getPosition().add(0, blockEntity.getPos().getY() + 1 / 64.0f, 0));
    }
}

