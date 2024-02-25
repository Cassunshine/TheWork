package cassunshine.thework.alchemy.circle.events.ring;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyRingSetColorEvent extends AlchemyRingEvent {
    public static Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "ring_set_color");

    private int color;

    public AlchemyRingSetColorEvent() {
        super(IDENTIFIER);
    }

    public AlchemyRingSetColorEvent(int color, AlchemyRing ring) {
        super(ring, IDENTIFIER);

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
    public void applyToRing(AlchemyRing ring) {
        super.applyToRing(ring);

        ring.color = color;
    }
}
