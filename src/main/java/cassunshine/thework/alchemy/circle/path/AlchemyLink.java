package cassunshine.thework.alchemy.circle.path;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.elements.Element;
import net.minecraft.nbt.NbtCompound;

/**
 * Connects two nodes that aren't on the same ring.
 */
public class AlchemyLink extends AlchemyPath {

    public final AlchemyCircle circle;

    public AlchemyNode sourceNode;
    public AlchemyNode destinationNode;


    public AlchemyLink(AlchemyCircle circle) {
        super(0);
        this.circle = circle;
    }

    @Override
    public void spawnParticle(Element element, float progress) {

    }

    public void updateLength() {
        length = (float)sourceNode.getPosition().distanceTo(destinationNode.getPosition());

        if(sourceNode.nodeType != AlchemyNodeTypes.NONE)
            length -= 0.5f;
        if(destinationNode.nodeType != AlchemyNodeTypes.NONE)
            length -= 0.5f;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("sourceRingIndex", sourceNode.ring.index);
        nbt.putInt("sourceNodeIndex", sourceNode.index);

        nbt.putInt("destinationRingIndex", destinationNode.ring.index);
        nbt.putInt("destinationNodeIndex", destinationNode.index);

        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        sourceNode = circle.rings.get(nbt.getInt("sourceRingIndex")).getNode(nbt.getInt("sourceNodeIndex"));
        destinationNode = circle.rings.get(nbt.getInt("destinationRingIndex")).getNode(nbt.getInt("destinationNodeIndex"));

        length = (float) sourceNode.getPosition().distanceTo(destinationNode.getPosition());

        updateLength();
    }

}
