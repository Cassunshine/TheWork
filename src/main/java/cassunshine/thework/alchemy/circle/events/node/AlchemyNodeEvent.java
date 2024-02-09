package cassunshine.thework.alchemy.circle.events.node;

import cassunshine.thework.alchemy.circle.events.ring.AlchemyRingEvent;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyNodeEvent extends AlchemyRingEvent {

    public int index;

    public AlchemyNodeEvent(Identifier id) {
        super(id);
    }

    public AlchemyNodeEvent(AlchemyNode node, Identifier id) {
        super(node.ring, id);

        index = node.index;
    }


    @Override
    public void applyToRing(AlchemyRing ring) {
        applyToNode(ring.nodes[ring.index]);
    }

    public void applyToNode(AlchemyNode node) {

    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeInt(index);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);
        index = buf.readInt();
    }
}