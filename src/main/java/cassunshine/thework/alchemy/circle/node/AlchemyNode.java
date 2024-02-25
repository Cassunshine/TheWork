package cassunshine.thework.alchemy.circle.node;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircleComponent;
import cassunshine.thework.alchemy.circle.events.circle.AlchemyCircleSetColorEvent;
import cassunshine.thework.alchemy.circle.events.circle.CreateLinkEvent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetColorEvent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetItemEvent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetSidesAndRune;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeType;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.alchemy.circle.path.AlchemyLink;
import cassunshine.thework.alchemy.circle.path.AlchemyPath;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.alchemy.elements.inventory.ElementInventory;
import cassunshine.thework.entities.InteractionPointEntity;
import cassunshine.thework.items.ChalkItem;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import cassunshine.thework.utils.TheWorkUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AlchemyNode implements AlchemyCircleComponent {
    public static final Identifier NULL_RUNE = new Identifier(TheWorkMod.ModID, "no_rune");

    /**
     * The ring this node is attached to.
     */
    public final AlchemyRing ring;

    public final int index;

    public int color = 0xFFFFFFFF;

    /**
     * Inventory holding all the elements the node currently has.
     */
    public final ElementInventory inventory = new ElementInventory();

    /**
     * Inventory to move to the .
     */
    public final ElementInventory ringOutput = new ElementInventory();

    /**
     * Inventory to move to outputs.
     */
    public final ElementInventory linkOutput = new ElementInventory();


    /**
     * The type of this node.
     */
    public AlchemyNodeType nodeType = AlchemyNodeTypes.NONE;

    /**
     * Custom data for this node, stored by the type.
     */
    public NbtCompound typeData = new NbtCompound();

    /**
     * Interaction point entity for nodes that hold items
     */
    public InteractionPointEntity interactionPoint;


    /**
     * The ItemStack this node is holding, if any.
     */
    public ItemStack heldStack = ItemStack.EMPTY;

    public AlchemyLink link = null;


    /**
     * The number of sides this node has.
     */
    public int sides = 0;
    public Identifier rune = TheWorkRunes.NULL;

    public AlchemyNode(AlchemyRing ring, int index) {
        this.ring = ring;
        this.index = index;
    }

    public float getAngle() {
        return (this.index / (float) ring.nodes.length) * MathHelper.TAU;
    }

    public Vec3d getPositionRelative() {
        return new Vec3d(MathHelper.sin(getAngle()) * ring.radius, 0, MathHelper.cos(getAngle()) * ring.radius);
    }

    public Vec3d getPositionFlat() {
        return ring.circle.blockEntity.flatPosition.add(getPositionRelative());
    }

    public Vec3d getPositionReal() {
        return getPositionFlat().withAxis(Direction.Axis.Y, ring.circle.blockEntity.getPos().getY());
    }

    public void setSidesAndRune(int sides, Identifier rune) {

        if (this.sides == sides && rune.equals(this.rune)) {
            sides = 0;
            rune = TheWorkRunes.NULL;
        }

        var newType = AlchemyNodeTypes.get(sides);

        this.sides = sides;
        this.rune = rune;

        nodeType = newType;
        typeData = newType.getDefaultData();

        //Only spawn an interaction entity if the type requires, and there is no rune set.
        if (newType.heldItemFilter != null) {
            if (interactionPoint == null)
                interactionPoint = ring.circle.blockEntity.addInteractionPoint(getPositionFlat().add(0, ring.circle.blockEntity.getPos().getY() + 1 / 64.0f, 0));
        } else {
            if (interactionPoint != null)
                interactionPoint = ring.circle.blockEntity.removeInteractionPoint(interactionPoint);
        }

        //Pop off any existing items.
        if (!heldStack.isEmpty() && interactionPoint == null) {
            var pos = getPositionFlat();
            TheWorkUtils.dropItem(ring.circle.blockEntity.getWorld(), heldStack, (float) pos.x, (float) ring.circle.blockEntity.getPos().getY() + 0.5f, (float) pos.z);
        }

        ring.circle.regenerateLayouts();
    }

    public boolean isInteractionInRange(ItemUsageContext context) {
        var interactPos = context.getHitPos().withAxis(Direction.Axis.Y, 0);
        var pos = getPositionFlat();

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

    /**
     * Attempts to either:
     * - Use an existing NBT tag on a chalk to link this and another node
     * - Create an NBT tag on the chalk to link this node to another node
     */
    private TheWorkNetworkEvent generateLinkEvent(ItemUsageContext context) {
        var stack = context.getStack();
        var nbt = stack.getNbt();

        //Check first if the tag for making links is present. If it is, we've already started to make a link, so finish it.
        if (nbt != null) {
            var desiredTag = nbt.getCompound("link_info");
            //Remove tag, we either use it in this block or discard it.
            nbt.remove("link_info");

            try {
                if (desiredTag != null) {
                    BlockPos circlePos = BlockPos.fromLong(desiredTag.getLong("circle_pos"));
                    int ringIndex = desiredTag.getInt("ring_index");
                    int nodeIndex = desiredTag.getInt("node_index");

                    var circle = ring.circle;
                    //If position on tag matches this circle's position (same entity)
                    if (circle.blockEntity.getPos().equals(circlePos)) {
                        var otherNode = circle.rings.get(ringIndex).getNode(nodeIndex);

                        return new CreateLinkEvent(circle, this, otherNode);
                    }
                }
            } catch (Exception e) {
                //Silently fail.
                return TheWorkNetworkEvents.SUCCESS;
            }
        }

        //If we haven't started making a link, but the player is sneaking, then start making one.
        if (context.getPlayer().isSneaking()) {
            NbtCompound linkInfo = new NbtCompound();

            //Store position of block entity so we don't link across circles.
            linkInfo.putLong("circle_pos", ring.circle.blockEntity.getPos().asLong());
            linkInfo.putInt("ring_index", ring.index);
            linkInfo.putInt("node_index", index);

            nbt.put("link_info", linkInfo);
            //Don't actually run anything, but don't run anything else, either. Just succeed.
            return TheWorkNetworkEvents.SUCCESS;
        }

        return TheWorkNetworkEvents.NONE;
    }

    /**
     * Attempts to change this node to the type described by items held by the player.
     */
    private TheWorkNetworkEvent generateChangeSidesAndRuneEvent(ItemUsageContext context) {
        var stack = context.getPlayer().getStackInHand(context.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);

        if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM) || !stack.hasNbt() || !stack.getOrCreateNbt().contains("node", NbtElement.COMPOUND_TYPE))
            return TheWorkNetworkEvents.NONE;

        var nodeNBT = stack.getOrCreateNbt().getCompound("node");
        var rune = TheWorkRunes.getRuneByID(nodeNBT.getInt("rune_id"));

        if (context.getStack().getItem() instanceof ChalkItem cI && color != cI.color)
            AlchemyCircleBlockEntity.sendCircleEvent(ring.circle.blockEntity, new AlchemyNodeSetColorEvent(cI.color, this));

        return new AlchemyNodeSetSidesAndRune(nodeNBT.getInt("sides"), rune, this);
    }

    @Override
    public int getColor() {
        return color;
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

        if (link != null)
            for (AlchemyPath.ElementInstance element : link.elements)
                ring.circle.addBackfire(element.element, 1);

        ring.circle.addBackfire(inventory);
        ring.circle.addBackfire(ringOutput);
        ring.circle.addBackfire(linkOutput);

        inventory.clear();
        ringOutput.clear();
        linkOutput.clear();
    }

    @Override
    public void onDestroy() {
        if (heldStack.isEmpty())
            return;

        var pos = getPositionReal();

        TheWorkUtils.dropItem(ring.circle.blockEntity.getWorld(), heldStack, (float) pos.x, (float) pos.y + 0.5f, (float) pos.z);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("sides", sides);
        nbt.put("type_data", typeData);
        nbt.putInt("color", color);

        nbt.putString("rune", rune.toString());

        nbt.put("item", heldStack.writeNbt(new NbtCompound()));
        nbt.put("inventory", inventory.writeNbt(new NbtCompound()));
        nbt.put("ring_output", ringOutput.writeNbt(new NbtCompound()));
        nbt.put("link_output", linkOutput.writeNbt(new NbtCompound()));

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        sides = nbt.getInt("sides");
        typeData = nbt.getCompound("typeData");
        color = nbt.getInt("color");

        nodeType = AlchemyNodeTypes.get(sides);

        rune = new Identifier(nbt.getString("rune"));

        heldStack = ItemStack.fromNbt(nbt.getCompound("item"));
        inventory.readNbt(nbt.getCompound("inventory"));
        ringOutput.readNbt(nbt.getCompound("ring_output"));
        linkOutput.readNbt(nbt.getCompound("link_output"));
    }

    @Override
    public TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context) {
        if (!isInteractionInRange(context))
            return TheWorkNetworkEvents.NONE;

        if (context.getStack().getItem() instanceof ChalkItem cI && color != cI.color && nodeType != AlchemyNodeTypes.NONE)
            return new AlchemyNodeSetColorEvent(cI.color, this);

        var linkInteraction = generateLinkEvent(context);
        if (linkInteraction != TheWorkNetworkEvents.NONE)
            return linkInteraction;

        var changeInteraction = generateChangeSidesAndRuneEvent(context);
        if (changeInteraction != TheWorkNetworkEvents.NONE)
            return changeInteraction;

        return TheWorkNetworkEvents.NONE;
    }

    @Override
    public TheWorkNetworkEvent generateInteractEvent(ItemUsageContext context) {
        if (!isInteractionInRange(context)) return TheWorkNetworkEvents.NONE;

        //If the type holds items, swap item with player item.
        if (nodeType.heldItemFilter != null && (context.getStack().isEmpty() || nodeType.heldItemFilter.test(context.getStack())))
            return swapItemWithPlayer(context);

        return TheWorkNetworkEvents.NONE;
    }

    @Override
    public void regenerateInteractionPoints(AlchemyCircleBlockEntity blockEntity) {
        nodeType = AlchemyNodeTypes.get(sides);
        if (nodeType.heldItemFilter != null)
            interactionPoint = blockEntity.addInteractionPoint(getPositionFlat().add(0, blockEntity.getPos().getY() + 1 / 64.0f, 0));
    }
}

