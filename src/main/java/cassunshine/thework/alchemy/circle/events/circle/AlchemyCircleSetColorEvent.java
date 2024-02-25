package cassunshine.thework.alchemy.circle.events.circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyCircleSetColorEvent extends AlchemyCircleEvent {
    public static Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "circle_set_color");

    private int color;

    public AlchemyCircleSetColorEvent() {
        super(IDENTIFIER);
    }

    public AlchemyCircleSetColorEvent(int color, AlchemyCircle circle) {
        super(circle, IDENTIFIER);

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
    public void applyToCircle(AlchemyCircle circle) {
        super.applyToCircle(circle);

        circle.color = color;
    }
}
