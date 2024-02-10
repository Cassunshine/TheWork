package cassunshine.thework.alchemy.circle.events.node;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyNodeSetTypeAndRune extends AlchemyNodeEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "node_set_rune_and_type");

    public Identifier type;
    public Identifier rune;

    public AlchemyNodeSetTypeAndRune() {
        super(IDENTIFIER);
    }

    public AlchemyNodeSetTypeAndRune(Identifier type, Identifier rune, AlchemyNode node) {
        super(node, IDENTIFIER);

        this.type = type;
        this.rune = rune;
    }


    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeIdentifier(type);
        buf.writeIdentifier(rune);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        type = buf.readIdentifier();
        rune = buf.readIdentifier();
    }

    @Override
    public void applyToNode(AlchemyNode node) {
        node.setTypeAndRune(type, rune);
    }
}

