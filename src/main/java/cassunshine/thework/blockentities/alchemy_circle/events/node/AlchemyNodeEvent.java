package cassunshine.thework.blockentities.alchemy_circle.events.node;

import cassunshine.thework.blockentities.alchemy_circle.events.ring.AlchemyRingEvent;
import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemy_circle.rings.AlchemyRing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class AlchemyNodeEvent extends AlchemyRingEvent {
    public int nodeIndex;

    public AlchemyNodeEvent(Identifier id) {
        super(id);
    }

    public AlchemyNodeEvent(AlchemyNode node, Identifier id) {
        super(node.ring, id);
        this.nodeIndex = node.index;
    }


    @Override
    public void applyToRing(AlchemyRing ring) {
        var node = ring.getNode(nodeIndex);

        applyToNode(node);
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);

        buf.writeInt(nodeIndex);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        nodeIndex = buf.readInt();
    }

    public void applyToNode(AlchemyNode node) {

    }
}
