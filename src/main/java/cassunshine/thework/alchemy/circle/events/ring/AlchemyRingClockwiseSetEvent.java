package cassunshine.thework.alchemy.circle.events.ring;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyRingClockwiseSetEvent extends AlchemyRingEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "ring_set_clockwise");

    public boolean isClockwise;

    public AlchemyRingClockwiseSetEvent() {
        super(IDENTIFIER);
    }

    public AlchemyRingClockwiseSetEvent(boolean isClockwise, AlchemyRing ring) {
        super(ring, IDENTIFIER);

        this.isClockwise = isClockwise;
    }


    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeBoolean(isClockwise);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        isClockwise = buf.readBoolean();
    }

    @Override
    public void applyToRing(AlchemyRing ring) {
        ring.isClockwise = isClockwise;
        ring.updatePathLengths();
        ring.circle.regenerateLayouts();
    }
}
