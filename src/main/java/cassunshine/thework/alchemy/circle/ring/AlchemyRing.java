package cassunshine.thework.alchemy.circle.ring;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.AlchemyCircleComponent;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.path.AlchemyPath;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;

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
     * Array of all nodes in the ring, in order of appearance.
     */
    public AlchemyNode[] nodes = new AlchemyNode[0];

    /**
     * Array of all paths in the ring, in order of appearance.
     */
    public AlchemyPath[] paths = new AlchemyPath[0];

    public AlchemyRing(AlchemyCircle circle, int index) {
        this.circle = circle;
        this.index = index;
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
        nbt.put("nodes", nodesList);
        nbt.put("paths", pathsList);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        setRadius(nbt.getFloat("radius"));
        var nodesList = nbt.getList("nodes", NbtElement.COMPOUND_TYPE);
        var pathsList = nbt.getList("paths", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < nodes.length; i++) {
            nodes[i].readNbt(nodesList.getCompound(i));
            paths[i].readNbt(pathsList.getCompound(i));
        }
    }
}
