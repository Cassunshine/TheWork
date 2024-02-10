package cassunshine.thework.alchemy.circle.ring;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.AlchemyCircleComponent;
import cassunshine.thework.alchemy.circle.events.ring.AlchemyRingClockwiseSet;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.path.AlchemyPath;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Rings are single, continuous linkings of alternating paths and nodes.
 */
public class AlchemyRing implements AlchemyCircleComponent {

    /**
     * Holds a reference to the alchemy circle this ring belongs to.
     */
    public final AlchemyCircle circle;

    public int index;

    /**
     * Radius of the ring itself.
     */
    public float radius = 1;

    /**
     * Circumference of the ring. Calculated. when radius is set.
     */
    public float circumference = MathHelper.TAU;


    /**
     * Holds if the ring will transfer elements clockwise or counterclockwise.
     */
    public boolean isClockwise = true;

    /**
     * Array of all nodes in the ring, in order of appearance.
     */
    public AlchemyNode[] nodes = new AlchemyNode[0];

    /**
     * Array of all paths in the ring, in order of appearance.
     */
    public AlchemyPath[] paths = new AlchemyPath[0];


    public AlchemyRing(AlchemyCircle circle) {
        this.circle = circle;
    }

    private void regenerateNodesAndPaths() {
        int nodeCount = radius <= 1 ? 0 : ((int) (radius / 2.0f)) * 4;
        float lengthPerPath = circumference / nodeCount;

        nodes = new AlchemyNode[nodeCount];
        paths = new AlchemyPath[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            nodes[i] = new AlchemyNode(this, i);
            paths[i] = new AlchemyPath(this, i, lengthPerPath);
        }
    }

    public void setRadius(float radius) {
        this.radius = radius;
        this.circumference = MathHelper.TAU * radius;

        regenerateNodesAndPaths();
    }


    //Gets the angle to a position in 3d space.
    //This ignores the Y axis.
    private float getAngle(Vec3d position) {
        position = position.withAxis(Direction.Axis.Y, 0);
        var delta = circle.blockEntity.fullPosition.subtract(position);

        var atan2 = MathHelper.atan2(-delta.z, delta.x);
        atan2 += MathHelper.HALF_PI;
        if (atan2 < 0) atan2 += MathHelper.TAU;
        if (atan2 > MathHelper.TAU) atan2 -= MathHelper.TAU;

        return (float) atan2;
    }

    private Vec3d nearestPoint(Vec3d position) {
        float angle = getAngle(position);
        return new Vec3d(MathHelper.sin(angle), circle.blockEntity.getPos().getY(), MathHelper.cos(angle));
    }


    public int getNextNodeIndex(int i) {
        return isClockwise ? i - 1 : i + 1;
    }

    public AlchemyNode getNode(int i) {
        if (i < 0)
            i += nodes.length;
        return nodes[i % nodes.length];
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtList nodesList = new NbtList();
        NbtList pathsList = new NbtList();

        for (int i = 0; i < nodes.length; i++) {
            var node = nodes[i];
            var path = paths[i];

            nodesList.add(node.writeNbt(new NbtCompound()));
            pathsList.add(path.writeNbt(new NbtCompound()));
        }

        nbt.putFloat("radius", radius);
        nbt.putBoolean("clockwise", isClockwise);
        nbt.put("nodes", nodesList);
        nbt.put("paths", pathsList);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        setRadius(nbt.getFloat("radius"));
        isClockwise = nbt.contains("clockwise", NbtElement.BYTE_TYPE) ? nbt.getBoolean("clockwise") : true;
        var nodesList = nbt.getList("nodes", NbtElement.COMPOUND_TYPE);
        var pathsList = nbt.getList("paths", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < nodes.length; i++) {
            nodes[i].readNbt(nodesList.getCompound(i));
            paths[i].readNbt(pathsList.getCompound(i));
        }
    }

    @Override
    public TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context) {
        //Pass to nodes first.
        for (AlchemyNode node : nodes) {
            var nodeEvent = node.generateChalkEvent(context);
            if (nodeEvent != TheWorkNetworkEvents.NONE)
                return nodeEvent;
        }

        var flatHitPos = context.getHitPos().withAxis(Direction.Axis.Y, 0);
        var hitRadius = flatHitPos.distanceTo(circle.blockEntity.fullPosition);

        var relativeRadius = hitRadius - radius;

        //If chalk hit point is too far, do nothing.
        if (Math.abs(relativeRadius) > 1.0f)
            return TheWorkNetworkEvents.NONE;

        return new AlchemyRingClockwiseSet(relativeRadius >= 0, this);
    }

    @Override
    public TheWorkNetworkEvent generateInteractEvent(ItemUsageContext context) {
        return TheWorkNetworkEvents.NONE;
    }

}
