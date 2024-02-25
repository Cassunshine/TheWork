package cassunshine.thework.alchemy.circle.events.node;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyNodeSetSidesAndRune extends AlchemyNodeEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "node_set_rune_and_type");

    public int sides;
    public Identifier rune;

    public AlchemyNodeSetSidesAndRune() {
        super(IDENTIFIER);
    }

    public AlchemyNodeSetSidesAndRune(int sides, Identifier rune, AlchemyNode node) {
        super(node, IDENTIFIER);

        this.sides = sides;
        this.rune = rune;
    }


    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeInt(sides);
        buf.writeIdentifier(rune);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        sides = buf.readInt();
        rune = buf.readIdentifier();
    }

    @Override
    public void applyToNode(AlchemyNode node) {
        super.applyToNode(node);
        node.setSidesAndRune(sides, rune);
        node.ring.updatePathLengths();
        node.ring.circle.updateLinkLengths();
    }
}

