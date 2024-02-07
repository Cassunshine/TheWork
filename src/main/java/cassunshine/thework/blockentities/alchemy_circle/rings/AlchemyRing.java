package cassunshine.thework.blockentities.alchemy_circle.rings;


import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleComponent;
import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.AlchemyNodeTypes;
import cassunshine.thework.items.TheWorkItems;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;

/**
 * Alchemy rings are the pieces of an alchemical circle that hold nodes, and transfer essence from one node to another.
 */
public class AlchemyRing implements AlchemyCircleComponent {

    /**
     * The block entity that holds this alchemy ring.
     */
    public final AlchemyCircleBlockEntity circle;

    /**
     * Radius of the ring.
     */
    public float radius = 1;

    /**
     * The circumference of the ring.
     * <p>
     * Cached for ease of use, and to save performance.
     */
    public float circumference;

    /**
     * True if the ring should send elements clockwise, false for counterclockwise.
     */
    public boolean isClockwise = true;

    /**
     * Stores if this ring has another ring ahead of it.
     */
    public boolean hasNextRing = false;

    /**
     * An array of all the nodes in this alchemy ring.
     */
    public AlchemyNode[] nodes;

    public AlchemyRing(AlchemyCircleBlockEntity blockEntity) {
        this.circle = blockEntity;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        this.circumference = MathHelper.TAU * radius;

        if (radius < 1) nodes = new AlchemyNode[0];
        else {
            var nodeCount = MathHelper.ceil(radius / 2) * 4;
            nodes = new AlchemyNode[nodeCount];

            for (int i = 0; i < nodes.length; i++) {
                float angle = (i / (float) nodes.length) * MathHelper.TAU;
                nodes[i] = new AlchemyNode(angle, this);
            }
        }
    }

    public Vec3d getPosition() {
        return circle.fullPosition;
    }

    public Vec3d getRadialPosition(float angle) {
        return circle.fullPosition.add(MathHelper.sin(angle) * radius, 0, MathHelper.cos(angle) * radius);
    }

    /**
     * Gets the real distance between a node at some index and some angle.
     */
    public float getNodeDistance(int index, float angle) {
        float nodeAnglePercent = getNode(index).rotation / MathHelper.TAU;
        float anglePercent = angle / MathHelper.TAU;

        float distPercent = Math.min(Math.abs(anglePercent - nodeAnglePercent), Math.abs((1 - anglePercent) - nodeAnglePercent));

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

    public float getClosestRadius(float angle) {
        int nearestNode = getNearestNodeIndex(angle);
        float getNodeDistance = getNodeDistance(nearestNode, angle);
        if (getNode(nearestNode).type == AlchemyNodeTypes.NONE) return radius;

        float nodeInfluence = MathHelper.clamp(0.5f - getNodeDistance, 0, 1);

        return radius - nodeInfluence;
    }


    public AlchemyNode getNode(int index) {
        if (nodes.length == 0) return null;

        return nodes[index % nodes.length];
    }

    private boolean intersects(BlockPos pos) {
        Box box = new Box(pos);

        double dMin = 0;
        double dMax = 0;

        {
            double a = MathHelper.square(circle.fullPosition.x - box.minX);
            double b = MathHelper.square(circle.fullPosition.x - box.maxX);

            dMax += Math.max(a, b);

            if (circle.fullPosition.x < box.minX) dMin += a;
            else if (circle.fullPosition.x > box.maxX) dMin += b;
        }

        {
            double a = MathHelper.square(circle.fullPosition.z - box.minZ);
            double b = MathHelper.square(circle.fullPosition.z - box.maxZ);

            dMax += Math.max(a, b);

            if (circle.fullPosition.z < box.minZ) dMin += a;
            else if (circle.fullPosition.z > box.maxZ) dMin += b;
        }

        float smallRadius = radius;
        float bigRadius = radius + 0.5f;

        return dMin < bigRadius * bigRadius && dMax >= smallRadius * smallRadius;
    }

    @Override
    public boolean validityCheck() {
        for (AlchemyNode node : nodes)
            if (!node.validityCheck())
                return false;

        return true;
    }

    @Override
    public void activate() {
        for (AlchemyNode node : nodes)
            node.activate();
    }

    @Override
    public void operate() {
        for (AlchemyNode node : nodes)
            node.operate();

        for (int i = 0; i < nodes.length; i++) {
            AlchemyNode currentNode = nodes[i];
            int nextIndex = isClockwise ? (i == nodes.length - 1 ? 0 : i + 1) : (i == 0 ? nodes.length - 1 : i - 1);
            AlchemyNode nextNode = nodes[nextIndex];

            //Move from current node's primary output to the next node.
            currentNode.nextNodeOutput.transferAll(nextNode.inventory, 1);
        }
    }

    @Override
    public void stop() {
        for (AlchemyNode node : nodes)
            node.stop();
    }

    /**
     * Tries to handle an interaction with this ring.
     *
     * @return True if the interaction was handled, false otherwise.
     */
    @Override
    public boolean handleInteraction(ItemUsageContext context) {
        Vec3d interactPos = context.getHitPos().withAxis(Direction.Axis.Y, getPosition().y);

        //Interaction relative to this ring.
        Vec3d delta = interactPos.subtract(getPosition());

        float distFromCenter = (float) delta.length();

        //Distance that interaction is from the ring itself.
        float difference = distFromCenter - radius;

        //If interaction point is outside this ring, we don't handle it.
        boolean intersection = intersects(context.getBlockPos().add(context.getSide().getVector()));
        if (!intersection) return false;

        delta = delta.normalize();

        //Angle on the circle that interaction took place.
        //Need to do some math to make it not negative and nicer to use.
        float angle = (float) Math.atan2(-delta.z, delta.x);
        angle += MathHelper.PI * 0.5f;
        if (angle < 0) angle += MathHelper.TAU;
        if (angle > MathHelper.TAU) angle -= MathHelper.TAU;

        int nearestIndex = getNearestNodeIndex(angle);
        AlchemyNode node = nodes[nearestIndex];

        if (node.handleInteraction(context)) return true;

        //If interacting with chalk, you can change the direction of the ring.
        if (context.getStack().getItem() == TheWorkItems.CHALK_ITEM) {
            if (context.getPlayer().isSneaking()) {
                circle.isOutward = !circle.isOutward;
            } else {
                if (difference > 0) {
                    isClockwise = true;
                } else {
                    isClockwise = false;
                }
            }
        }

        return true;
    }

    @Override
    public void regenerateInteractionPoints() {
        for (AlchemyNode node : nodes)
            node.regenerateInteractionPoints();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat("radius", radius);
        nbt.putBoolean("clockwise", isClockwise);

        NbtList nodeList = new NbtList();
        nbt.put("nodes", nodeList);

        for (AlchemyNode node : nodes) {
            NbtCompound nodeCompound = new NbtCompound();
            nodeList.add(nodeCompound);

            node.writeNbt(nodeCompound);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        setRadius(nbt.getFloat("radius"));
        isClockwise = nbt.getBoolean("clockwise");

        NbtList nodeList = nbt.getList("nodes", NbtElement.COMPOUND_TYPE);

        int index = 0;
        for (NbtElement element : nodeList) {
            if (!(element instanceof NbtCompound nodeCompound)) return;

            AlchemyNode node = nodes[index++];
            node.readNbt(nodeCompound);
        }
    }
}
