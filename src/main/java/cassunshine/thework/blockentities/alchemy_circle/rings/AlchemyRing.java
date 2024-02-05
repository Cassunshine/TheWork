package cassunshine.thework.blockentities.alchemy_circle.rings;


import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeType;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeTypes;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

/**
 * Alchemy rings are the pieces of an alchemic circle that hold nodes, and transfer essence from one node to another.
 */
public class AlchemyRing {

    /**
     * Radius of the ring.
     */
    public float radius = 1;

    /**
     * The circumference of the ring.
     */
    public float circumference;

    /**
     * True if the ring should send essence clockwise, false for counterclockwise.
     */
    public boolean isClockwise = true;

    /**
     * Position of the ring's center in world-space.
     */
    public Vec3d position;

    /**
     * An array of all the nodes in this alchemy ring.
     */
    public AlchemyNode[] nodes;


    public void setRadius(float radius) {
        this.radius = radius;
        this.circumference = MathHelper.TAU * radius;

        if (radius < 1) nodes = new AlchemyNode[0];
        else {
            var nodeCount = MathHelper.ceil(radius / 2) * 4;
            nodes = new AlchemyNode[nodeCount];

            for (int i = 0; i < nodes.length; i++) {
                float angle = (i / (float) nodes.length) * MathHelper.TAU;
                nodes[i] = new AlchemyNode(position.add(MathHelper.sin(angle), 0, MathHelper.cos(angle)), angle);
            }
        }
    }

    /**
     * Tries to handle an interaction with this ring.
     *
     * @param interactPos Position of interaction. Y value will be ignored.
     * @return True if the interaction was handled, false otherwise.
     */
    public boolean handleInteraction(ItemUsageContext context, Vec3d interactPos) {
        interactPos = interactPos.withAxis(Direction.Axis.Y, position.y);

        //Interaction relative to this ring.
        Vec3d delta = interactPos.subtract(position);

        //Distance that interaction is from the ring itself.
        float difference = (float) delta.length() - radius;
        float interactDist = MathHelper.abs(difference);

        //Interactions further than 0.5 blocks away will fail.
        if (interactDist > 0.5f) return false;

        //Angle on the circle that interaction took place.
        //Need to do some math to make it not negative and nicer to use.
        float angle = (float) Math.atan2(-delta.z, delta.x);
        angle += MathHelper.PI * 0.5f;
        if (angle < 0) angle += MathHelper.TAU;

        int nearestIndex = getNearestNodeIndex(angle);
        AlchemyNode node = nodes[nearestIndex];

        if (node.handleInteraction(context, interactPos)) return true;

        if (difference > 0) {
            isClockwise = true;
        } else {
            isClockwise = false;
        }

        return true;
    }


    /**
     * Gets the real distance between a node at some index and some angle.
     */
    public float getNodeDistance(int index, float angle) {
        float nodePercent = index / (float) nodes.length;

        float nodeAngle = MathHelper.TAU * nodePercent;
        float anglePercent = angle / MathHelper.TAU;

        float distPercent = Math.min(Math.abs(anglePercent - nodeAngle), Math.abs((1 - anglePercent) - nodeAngle));

        return circumference * distPercent;
    }

    /**
     * Takes some angle and returns the node nearest to that angle.
     */
    public int getNearestNodeIndex(float angle) {
        float anglePercent = angle / MathHelper.TAU;
        return Math.round(anglePercent * nodes.length) % nodes.length;
    }

    /**
     * Takes some angle and returns the node that comes after it on the circle.
     */
    public int getNextNodeIndex(float angle) {
        float anglePercent = angle / MathHelper.TAU;
        return MathHelper.ceil(anglePercent * nodes.length) % nodes.length;
    }

    /**
     * Takes some angle and returns the node that comes before it on the circle.
     */
    public int getLastNodeIndex(float angle) {
        float anglePercent = angle / MathHelper.TAU;
        return MathHelper.floor(anglePercent * nodes.length) % nodes.length;
    }


    public void readNBT(NbtCompound nbt) {

    }

    public void writeNbt(NbtCompound nbt){

    }
}
