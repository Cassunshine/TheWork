package cassunshine.thework.blockentities.alchemy_circle.events.ring;

import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.alchemy_circle.events.circle.AlchemyCircleEvent;
import cassunshine.thework.blockentities.alchemy_circle.rings.AlchemyRing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class AlchemyRingEvent extends AlchemyCircleEvent {
    public int ringIndex = 0;

    public AlchemyRingEvent(Identifier id) {
        super(id);
    }

    public AlchemyRingEvent(AlchemyRing ring, Identifier id) {
        super(ring.circle, id);
        this.ringIndex = ring.index;
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);

        buf.writeInt(ringIndex);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        ringIndex = buf.readInt();
    }

    @Override
    public void applyToCircle(AlchemyCircleBlockEntity target) {
        var ring = target.rings.get(ringIndex);
        applyToRing(ring);
    }

    public void applyToRing(AlchemyRing ring) {

    }
}
