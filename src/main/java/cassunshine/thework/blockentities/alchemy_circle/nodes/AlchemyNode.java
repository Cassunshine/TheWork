package cassunshine.thework.blockentities.alchemy_circle.nodes;


import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.connections.ConnectionType;
import cassunshine.thework.blockentities.alchemy_circle.rings.AlchemyRing;
import cassunshine.thework.elements.Element;
import cassunshine.thework.elements.TheWorkElements;
import cassunshine.thework.items.TheWorkItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * Nodes are the main points of interaction with alchemic circles
 * They act as the input, output, processing, and pools of circles.
 */
public class AlchemyNode {

    public final AlchemyRing ring;

    /**
     * Position of the node in 3D space.
     */
    public Vec3d position;

    /**
     * Rotation angle of the node.
     */
    public float rotation;

    /**
     * The type of node.
     */
    public NodeType type = NodeTypes.NONE;

    /**
     * The connection type out to the next ring, if any.
     */
    public ConnectionType outConnectionType = ConnectionType.ALLOW;

    /**
     * The connection type to the next node, if any.
     */
    public ConnectionType nextConnectionType = ConnectionType.ALLOW;

    public Element currentElement = TheWorkElements.NONE;

    public float elementAmount = 0;

    public ItemStack item = ItemStack.EMPTY;

    public Entity entity;

    public AlchemyNode(Vec3d pos, float rotation, AlchemyRing ring) {
        position = pos;
        this.ring = ring;
        this.rotation = rotation;
    }


    public boolean handleInteraction(ItemUsageContext context, Vec3d interactPos) {
        //Nodes have a radius of 0.5 blocks
        if (interactPos.distanceTo(position) > 0.5f)
            return false;

        //Change the type, if we're holding chalk.
        if (context.getStack().getItem() == TheWorkItems.CHALK_ITEM) {
            //Check if we're changing the node's type.
            //If we do, don't interact with the node aside from this.
            var newType = getDesiredNodeType(context);
            setType(newType, true);

            return true;
        }

        //If we're not setting the type, try to interact with it.
        return type.handleInteraction(this, context);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("type", type.id.toString());
        nbt.putString("out_type", outConnectionType.toString());
        nbt.putString("next_type", nextConnectionType.toString());

        nbt.putString("element", currentElement.id.toString());
        nbt.putFloat("element_amount", elementAmount);

        try{
            if (item.isEmpty())
                return;

            var iNbt = item.writeNbt(new NbtCompound());
            nbt.put("item", iNbt);
        } catch (Exception e){
            TheWorkMod.LOGGER.error(e.toString());
        }
    }

    public void readNbt(NbtCompound nbt) {
        setType(NodeTypes.getType(new Identifier(nbt.getString("type"))), false);
        outConnectionType = ConnectionType.valueOf(nbt.getString("out_type"));
        nextConnectionType = ConnectionType.valueOf(nbt.getString("next_type"));

        currentElement = TheWorkElements.getElement(new Identifier(nbt.getString("element")));
        elementAmount = nbt.getFloat("element_amount");

        if (nbt.contains("item"))
            item = ItemStack.fromNbt(nbt.getCompound("item"));
    }

    private void setType(NodeType newType, boolean dropItems) {
        if (newType != type) {
            type = newType;

            if (dropItems && !item.isEmpty()) {
                ItemEntity entity = new ItemEntity(ring.blockEntity.getWorld(), position.x, position.y, position.z, item.copyAndEmpty());
                ring.blockEntity.getWorld().spawnEntity(entity);
            }

            regenerateInteractionPoints();
        }
    }


    private NodeType getDesiredNodeType(ItemUsageContext context) {
        Hand oppositeHand = context.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack offhandStack = context.getPlayer().getStackInHand(oppositeHand);

        if (offhandStack.getItem() != Items.WRITABLE_BOOK || !offhandStack.hasNbt())
            return NodeTypes.NONE;

        try {
            NbtList list = offhandStack.getNbt().getList("pages", NbtElement.STRING_TYPE);

            //Get string from book, trim whitespaces.
            String firstPage = list.getString(0);
            firstPage = firstPage.trim();

            Identifier id = new Identifier(TheWorkMod.ModID, firstPage);

            NodeType newType = NodeTypes.getType(id);

            return newType == type ? NodeTypes.NONE : newType;
        } catch (Exception e) {
            //Ignore
        }

        return NodeTypes.NONE;
    }

    public void regenerateInteractionPoints() {
        //Delete old interaction entity, if there was one.
        if (entity != null)
            ring.blockEntity.removeInteractionPoint(entity);

        if (type.requireEntity)
            entity = this.ring.blockEntity.addInteractionPoint(position);
    }

    @Override
    public String toString() {
        return "Alchemy Node With Type " + type;
    }
}
