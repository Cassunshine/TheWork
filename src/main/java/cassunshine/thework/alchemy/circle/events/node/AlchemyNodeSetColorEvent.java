package cassunshine.thework.alchemy.circle.events.node;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyNodeSetColorEvent extends AlchemyNodeEvent {
    public static Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "node_set_color");

    private int color;

    public AlchemyNodeSetColorEvent(Identifier id) {
        super(id);
    }

    public AlchemyNodeSetColorEvent() {
        super(IDENTIFIER);
    }

    public AlchemyNodeSetColorEvent(int color, AlchemyNode node) {
        super(node, IDENTIFIER);

        this.color = color;
    }

    public AlchemyNodeSetColorEvent(Identifier id, int color, AlchemyNode node) {
        super(node, IDENTIFIER);

        this.color = color;
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeInt(color);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        color = buf.readInt();
    }

    @Override
    public void applyToNode(AlchemyNode node) {
        super.applyToNode(node);

        node.color = color;
    }
}
