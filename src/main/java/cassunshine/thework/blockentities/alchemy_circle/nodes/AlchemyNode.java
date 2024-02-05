package cassunshine.thework.blockentities.alchemy_circle.nodes;


import cassunshine.thework.blockentities.alchemy_circle.connections.ConnectionType;
import cassunshine.thework.elements.Element;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

/**
 * Nodes are the main points of interaction with alchemic circles
 * They act as the input, output, processing, and pools of circles.
 */
public class AlchemyNode {

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

    public Element currentElement = null;

    public float elementAmount = 0;


    public AlchemyNode(Vec3d pos, float rotation) {
        position = pos;
    }


    public boolean handleInteraction(ItemUsageContext context, Vec3d interactPos) {
        if (interactPos.distanceTo(position) > 0.5)
            return false;


        return true;
    }


    public void readNbt(NbtCompound nbt) {

    }

    public void writeNbt(NbtCompound nbt){

    }
}
