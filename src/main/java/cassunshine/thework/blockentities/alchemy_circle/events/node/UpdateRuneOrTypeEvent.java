package cassunshine.thework.blockentities.alchemy_circle.events.node;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UpdateRuneOrTypeEvent extends AlchemyNodeEvent {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "event/update_rune_or_type");

    private Identifier typeId;
    private Identifier runeId;

    public UpdateRuneOrTypeEvent() {
        super(IDENTIFIER);
    }

    public UpdateRuneOrTypeEvent(Identifier newType, Identifier newRune, AlchemyNode alchemyNode) {
        super(alchemyNode, IDENTIFIER);

        typeId = newType;
        runeId = newRune;
    }

    @Override
    public void applyToNode(AlchemyNode node) {
        node.setType(typeId);
        node.setRune(runeId);
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);

        buf.writeIdentifier(typeId);
        buf.writeIdentifier(runeId);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        typeId = buf.readIdentifier();
        runeId = buf.readIdentifier();
    }
}
