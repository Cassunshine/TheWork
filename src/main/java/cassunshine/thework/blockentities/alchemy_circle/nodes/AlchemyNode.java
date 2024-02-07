package cassunshine.thework.blockentities.alchemy_circle.nodes;


import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleComponent;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.AlchemyNodeType;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.AlchemyNodeTypes;
import cassunshine.thework.blockentities.alchemy_circle.rings.AlchemyRing;
import cassunshine.thework.elements.Element;
import cassunshine.thework.elements.Elements;
import cassunshine.thework.elements.inventory.ElementInventory;
import cassunshine.thework.items.TheWorkItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Nodes are the main points of interaction with alchemic circles
 * They act as the input, output, processing, and pools of circles.
 */
public class AlchemyNode implements AlchemyCircleComponent {

    /**
     * The ring this element is attached to.
     */
    public final AlchemyRing ring;

    /**
     * 3D position of the node.
     */
    public final Vec3d position;

    /**
     * Primary elemental inventory of this node.
     */
    public final ElementInventory inventory = new ElementInventory(128);

    /**
     * The output of this node that will be transferred to the next node in the ring at the end of the tick.
     */
    public final ElementInventory nextNodeOutput = new ElementInventory(1);

    /**
     * The output of this node that will be transferred to the parallel ring at the end of the tick.
     */
    public final ElementInventory parallelOutput = new ElementInventory(1);

    /**
     * Rotation angle of the node.
     */
    public float rotation;

    /**
     * The type of node.
     */
    public AlchemyNodeType type = AlchemyNodeTypes.NONE;

    /**
     * Some node types can hold items, so this stores a single stack for this node.
     */
    public ItemStack item = ItemStack.EMPTY;

    /**
     * The rune to draw on the node, if any.
     */
    public Identifier rune = new Identifier(TheWorkMod.ModID, "none");

    /**
     * The interaction entity this node created last, if any.
     */
    public Entity interactionEntity;


    /**
     * Used to do actions a limited number of times per second for node types.
     */
    public int cooldown = 0;

    public AlchemyNode(float rotation, AlchemyRing ring) {
        this.ring = ring;
        this.rotation = rotation;

        this.position = ring.getRadialPosition(rotation);
    }

    private void setType(AlchemyNodeType newType) {
        if (newType != type) {
            type = newType;

            //Drop item due to changed type.
            if (!item.isEmpty()) {
                ItemEntity entity = new ItemEntity(ring.circle.getWorld(), position.x, position.y, position.z, item.copyAndEmpty());
                ring.circle.getWorld().spawnEntity(entity);
            }

            regenerateInteractionPoints();
        }
    }

    private void setRune(Identifier id) {
        if (rune != id) {
            rune = id;

            //Drop item due to changed type.
            if (!item.isEmpty()) {
                ItemEntity entity = new ItemEntity(ring.circle.getWorld(), position.x, position.y, position.z, item.copyAndEmpty());
                ring.circle.getWorld().spawnEntity(entity);
            }
        }
    }

    private String getFirstPage(ItemUsageContext context) {
        Hand oppositeHand = context.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack offhandStack = context.getPlayer().getStackInHand(oppositeHand);

        if (offhandStack.getItem() != Items.WRITABLE_BOOK || !offhandStack.hasNbt())
            return null;

        try {
            NbtList list = offhandStack.getNbt().getList("pages", NbtElement.STRING_TYPE);

            //Get string from book, trim whitespaces.
            String firstPage = list.getString(0);
            firstPage = firstPage.trim();

            return firstPage;
        } catch (Exception e) {
            //Ignore
        }

        return null;
    }

    /**
     * Uses the off-hand item to determine the type of rune (written book for now)
     */
    private AlchemyNodeType getDesiredNodeType(ItemUsageContext context) {
        var firstPage = getFirstPage(context);

        if (firstPage == null)
            return AlchemyNodeTypes.NONE;

        var potentialID = new Identifier(TheWorkMod.ModID, firstPage);

        var newType = AlchemyNodeTypes.getType(potentialID);

        return newType == type ? AlchemyNodeTypes.NONE : newType;
    }

    private Identifier getDesiredRune(ItemUsageContext context) {
        var firstPage = getFirstPage(context);

        if (firstPage == null)
            return new Identifier(TheWorkMod.ModID, "none");

        var potentialID = new Identifier(TheWorkMod.ModID, firstPage);

        if (potentialID.equals(rune))
            return new Identifier(TheWorkMod.ModID, "none");

        var element = Elements.getElement(potentialID);

        return element != Elements.NONE ? potentialID : new Identifier(TheWorkMod.ModID, "none");
    }

    @Override
    public boolean validityCheck() {
        return type.validityCheck(this);
    }

    @Override
    public void activate() {
        type.activate(this);
    }

    @Override
    public void operate() {
        if (cooldown > 0)
            cooldown--;
        type.operate(this);
    }

    @Override
    public void stop() {
        type.stop(this);

        cooldown = 0;
        inventory.flush();
        nextNodeOutput.flush();
        parallelOutput.flush();
    }

    @Override
    public void regenerateInteractionPoints() {
        //Delete old interaction entity, if there was one.
        if (interactionEntity != null)
            ring.circle.removeInteractionPoint(interactionEntity);

        if (type.requireInteractionEntity)
            interactionEntity = this.ring.circle.addInteractionPoint(position);
    }

    @Override
    public boolean handleInteraction(ItemUsageContext context) {
        Vec3d interactPos = context.getHitPos().withAxis(Direction.Axis.Y, position.y);

        //Nodes have a radius of 0.5 blocks
        if (interactPos.distanceTo(position) > 0.5f)
            return false;

        //Change the type, if we're holding chalk.
        if (context.getStack().getItem() == TheWorkItems.CHALK_ITEM) {
            var newType = getDesiredNodeType(context);
            var newRune = getDesiredRune(context);

            //If the new rune is different from the old one, set it.
            if (!newRune.equals(rune))
                setRune(newRune);
            else
                setType(newType);

            return true;
        }


        //If we're not setting the type, try to interact with it.
        if (type.handleInteraction(this, context))
            return true;

        //If nothing else is handled, swap the item in the node.
        if (type.requireInteractionEntity) {
            var hand = context.getStack().copyAndEmpty();
            var inv = item.copyAndEmpty();

            context.getPlayer().equipStack(context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, inv);
            item = hand;
            rune = new Identifier(TheWorkMod.ModID, "none");
        }

        //Still handle the input, in either case.
        return true;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("type", type.id.toString());
        nbt.putString("rune", rune.toString());

        nbt.put("inventory", inventory.writeNbt(new NbtCompound()));
        nbt.put("next_inventory", nextNodeOutput.writeNbt(new NbtCompound()));
        nbt.put("parallel_inventory", parallelOutput.writeNbt(new NbtCompound()));

        try {
            if (item.isEmpty())
                return;

            var iNbt = item.writeNbt(new NbtCompound());
            nbt.put("item", iNbt);
        } catch (Exception e) {
            TheWorkMod.LOGGER.error(e.toString());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        setType(AlchemyNodeTypes.getType(new Identifier(nbt.getString("type"))));
        setRune(new Identifier(nbt.getString("rune")));

        inventory.readNbt(nbt.getCompound("inventory"));
        nextNodeOutput.readNbt(nbt.getCompound("next_inventory"));
        parallelOutput.readNbt(nbt.getCompound("parallel_inventory"));

        if (nbt.contains("item"))
            item = ItemStack.fromNbt(nbt.getCompound("item"));
    }

    @Override
    public String toString() {
        return "Alchemy Node With Type " + type;
    }
}
