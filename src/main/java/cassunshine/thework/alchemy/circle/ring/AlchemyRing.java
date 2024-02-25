package cassunshine.thework.alchemy.circle.ring;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.AlchemyCircleComponent;
import cassunshine.thework.alchemy.circle.events.circle.AlchemyCircleSetColorEvent;
import cassunshine.thework.alchemy.circle.events.ring.AlchemyRingClockwiseSetEvent;
import cassunshine.thework.alchemy.circle.events.ring.AlchemyRingSetColorEvent;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.alchemy.circle.path.AlchemyPath;
import cassunshine.thework.alchemy.circle.path.AlchemyRingPath;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.alchemy.elements.inventory.ElementInventory;
import cassunshine.thework.items.ChalkItem;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

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
    public AlchemyRingPath[] paths = new AlchemyRingPath[0];

    public int color = 0xFFFFFFFF;


    public AlchemyRing(AlchemyCircle circle) {
        this.circle = circle;
    }

    private void regenerateNodesAndPaths() {
        int nodeCount = radius <= 1 ? 0 : ((int) (radius)) * 4;
        float lengthPerPath = circumference / nodeCount;

        nodes = new AlchemyNode[nodeCount];
        paths = new AlchemyRingPath[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            nodes[i] = new AlchemyNode(this, i);
            paths[i] = new AlchemyRingPath(this, i, lengthPerPath);
        }

        updatePathLengths();
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
        var delta = circle.blockEntity.flatPosition.subtract(position);

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
        if (i < 0) i += nodes.length;
        return nodes[i % nodes.length];
    }

    public void updatePathLengths() {

        var nodeWidthAngle = MathHelper.lerp(0.5f / circumference, 0, MathHelper.TAU);

        for (int i = 0; i < paths.length; i++) {
            var pathThis = paths[i];
            var nodeThis = this.getNode(i);
            var nodeNext = this.getNode(this.getNextNodeIndex(i));

            var pathLength = circumference / paths.length;

            var pathStart = nodeThis.getAngle();
            var pathEnd = nodeNext.getAngle();

            if (nodeThis.sides != 0) {
                pathLength -= 0.5f;
                pathStart += isClockwise ? -nodeWidthAngle : nodeWidthAngle;
            }

            if (nodeNext.sides != 0) {
                pathLength -= 0.5f;
                pathEnd += isClockwise ? nodeWidthAngle : -nodeWidthAngle;
            }

            pathThis.length = pathLength;
            pathThis.startAngle = pathStart;
            pathThis.endAngle = pathEnd;
        }
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void activate() {
        for (AlchemyNode node : nodes) {
            node.activate();
            if (node.link != null)
                node.link.activate();
        }
        for (AlchemyPath path : paths)
            path.activate();
    }

    @Override
    public void activeTick() {
        for (AlchemyNode node : nodes) {
            node.activeTick();
            if (node.link != null)
                node.link.activeTick();
        }
        for (AlchemyPath path : paths)
            path.activeTick();

        ArrayList<Element> finishedPathElements = new ArrayList<>();

        for (int i = 0; i < nodes.length; i++) {
            //Get current node, current path, and next node.
            var nodeThis = getNode(i);
            var path = paths[i];
            var nextNode = getNode(getNextNodeIndex(i));
            var link = nodeThis.link;

            //Move elements from the link inventory along the link, if it exists.
            if (link != null) {
                transferElements(nodeThis.linkOutput, link);
            } else {
                nodeThis.linkOutput.transfer(nodeThis.ringOutput, Float.POSITIVE_INFINITY);
            }

            //Move elements from the output inventory along the ring.
            transferElements(nodeThis.ringOutput, path);

            //Collect elements that have finished moving along the path.
            finishedPathElements.clear();
            path.removeFinishedElements(finishedPathElements);

            //Loop over all elements at the end of the path.
            for (Element element : finishedPathElements) {
                //If rolled number is lower than the chaos, backfire this element.
                if (circle.blockEntity.getWorld().random.nextFloat() < circle.circleChaosSquare) {
                    circle.addBackfire(element, 1);
                    continue;
                }

                //Put the element in the node, recording the leftover.
                var leftover = nextNode.inventory.put(element, 1);

                //Backfire anything that didn't fit.
                circle.addBackfire(element, leftover);
            }

            if (link != null) {
                //Collect elements that have finished moving along the link.
                finishedPathElements.clear();
                link.removeFinishedElements(finishedPathElements);

                //Loop over all elements at the end of the link.
                for (Element element : finishedPathElements) {
                    //If rolled number is lower than the chaos, backfire this element.
                    if (circle.blockEntity.getWorld().random.nextFloat() < circle.circleChaosSquare) {
                        circle.addBackfire(element, 1);
                        continue;
                    }

                    //Put the element in the node, recording the leftover.
                    var leftover = link.destinationNode.inventory.put(element, 1);

                    //Backfire anything that didn't fit.
                    circle.addBackfire(element, leftover);
                }
            }
        }
    }

    private void transferElements(ElementInventory inventory, AlchemyPath path) {
        for (int elementNumber = 1; elementNumber < Elements.getElementCount(); elementNumber++) {
            var element = Elements.getElement(elementNumber);

            //Only move in discreet packets of 1 at a time.
            if (!inventory.give(element, 1))
                continue;

            //Add to path.
            path.addElement(element, 0);
            break;
        }
    }

    @Override
    public void deactivate() {
        for (AlchemyNode node : nodes) {
            node.deactivate();
            if (node.link != null)
                node.link.deactivate();
        }
        for (AlchemyPath path : paths)
            path.deactivate();
    }

    @Override
    public void onDestroy() {
        for (AlchemyNode node : nodes) {
            node.onDestroy();
            if (node.link != null)
                node.link.onDestroy();
        }
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
        nbt.putInt("color", color);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        setRadius(nbt.getFloat("radius"));
        color = nbt.getInt("color");
        isClockwise = nbt.contains("clockwise", NbtElement.BYTE_TYPE) ? nbt.getBoolean("clockwise") : true;
        var nodesList = nbt.getList("nodes", NbtElement.COMPOUND_TYPE);
        var pathsList = nbt.getList("paths", NbtElement.COMPOUND_TYPE);

        //There are always an equal number of paths and nodes.
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].readNbt(nodesList.getCompound(i));
            paths[i].readNbt(pathsList.getCompound(i));
        }

        updatePathLengths();
    }

    @Override
    public TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context) {
        //Pass to nodes first.
        for (AlchemyNode node : nodes) {
            var nodeEvent = node.generateChalkEvent(context);
            if (nodeEvent != TheWorkNetworkEvents.NONE) return nodeEvent;
        }

        var flatHitPos = context.getHitPos().withAxis(Direction.Axis.Y, 0);
        var hitRadius = flatHitPos.distanceTo(circle.blockEntity.flatPosition);

        var relativeRadius = hitRadius - radius;

        //If chalk hit point is too far, do nothing.
        if (Math.abs(relativeRadius) > 1.0f) return TheWorkNetworkEvents.NONE;

        if (context.getStack().getItem() instanceof ChalkItem cI && color != cI.color)
            AlchemyCircleBlockEntity.sendCircleEvent(circle.blockEntity, new AlchemyRingSetColorEvent(cI.color, this));

        return new AlchemyRingClockwiseSetEvent(relativeRadius >= 0, this);
    }

    @Override
    public TheWorkNetworkEvent generateInteractEvent(ItemUsageContext context) {
        for (AlchemyNode node : nodes) {
            var event = node.generateInteractEvent(context);
            if (event != TheWorkNetworkEvents.NONE) return event;
        }
        return TheWorkNetworkEvents.NONE;
    }

    @Override
    public void regenerateInteractionPoints(AlchemyCircleBlockEntity blockEntity) {
        for (AlchemyNode node : nodes)
            node.regenerateInteractionPoints(blockEntity);
    }
}
