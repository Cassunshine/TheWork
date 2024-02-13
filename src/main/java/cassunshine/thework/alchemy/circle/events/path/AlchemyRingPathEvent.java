package cassunshine.thework.alchemy.circle.events.path;

import cassunshine.thework.alchemy.circle.events.ring.AlchemyRingEvent;
import cassunshine.thework.alchemy.circle.path.AlchemyPath;
import cassunshine.thework.alchemy.circle.path.AlchemyRingPath;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyRingPathEvent extends AlchemyRingEvent {
    public int index;

    public AlchemyRingPathEvent(Identifier id) {
        super(id);
    }

    public AlchemyRingPathEvent(AlchemyRingPath path, Identifier id) {
        super(path.ring, id);
        index = path.index;
    }


    @Override
    public void applyToRing(AlchemyRing ring) {
        applyToPath(ring.paths[index]);
    }

    public void applyToPath(AlchemyRingPath path) {
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);
        buf.writeInt(index);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);
        index = buf.readInt();
    }
}