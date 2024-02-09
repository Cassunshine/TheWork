package cassunshine.thework.alchemy.circle.events.ring;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.events.circle.AlchemyCircleEvent;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyRingEvent extends AlchemyCircleEvent {
    public int index;

    public AlchemyRingEvent(Identifier id) {
        super(id);
    }

    public AlchemyRingEvent(AlchemyRing ring, Identifier id) {
        super(ring.circle, id);

        index = ring.index;
    }


    @Override
    public void applyToCircle(AlchemyCircle circle) {

    }

    public void applyToRing(AlchemyRing ring) {

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
