package cassunshine.thework.alchemy.circle.events.path;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.path.AlchemyRingPath;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyRingPathSetColorEvent extends AlchemyRingPathEvent {
    public static Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "node_set_color");

    private int color;

    public AlchemyRingPathSetColorEvent() {
        super(IDENTIFIER);
    }

    public AlchemyRingPathSetColorEvent(int color, AlchemyRingPath path) {
        super(path, IDENTIFIER);

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
    public void applyToPath(AlchemyRingPath path) {
        super.applyToPath(path);

        path.color = color;
    }
}
