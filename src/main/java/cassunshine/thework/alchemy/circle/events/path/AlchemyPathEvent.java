package cassunshine.thework.alchemy.circle.events.path;

import cassunshine.thework.alchemy.circle.events.ring.AlchemyRingEvent;
import cassunshine.thework.alchemy.circle.path.AlchemyPath;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyPathEvent extends AlchemyRingEvent {
    public int index;

    public AlchemyPathEvent(Identifier id) {
        super(id);
    }

    public AlchemyPathEvent(AlchemyPath path, Identifier id) {
        super(path.ring, id);
        index = path.index;
    }


    @Override
    public void applyToRing(AlchemyRing ring) {
        applyToPath(ring.paths[index]);
    }

    public void applyToPath(AlchemyPath path) {

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