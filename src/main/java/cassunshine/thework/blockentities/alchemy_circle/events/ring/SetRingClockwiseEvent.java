package cassunshine.thework.blockentities.alchemy_circle.events.ring;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.rings.AlchemyRing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SetRingClockwiseEvent extends AlchemyRingEvent {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "event/set_ring_clockwise");

    public boolean isClockwise;

    public SetRingClockwiseEvent() {
        super(IDENTIFIER);
    }

    public SetRingClockwiseEvent(boolean clockwise, AlchemyRing ring) {
        super(ring, IDENTIFIER);

        isClockwise = clockwise;
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);

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
    }
}
