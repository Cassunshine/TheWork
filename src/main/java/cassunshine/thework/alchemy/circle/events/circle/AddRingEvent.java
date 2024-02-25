package cassunshine.thework.alchemy.circle.events.circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AddRingEvent extends AlchemyCircleEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "circle_add_ring");

    public float radius;
    public int color;

    public AddRingEvent() {
        super(IDENTIFIER);
    }

    public AddRingEvent(float radius, int color, AlchemyCircle circle) {
        super(circle, IDENTIFIER);

        this.radius = radius;
        this.color = color;
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeFloat(radius);
        buf.writeInt(color);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        radius = buf.readFloat();
        color = buf.readInt();
    }

    @Override
    public void applyToCircle(AlchemyCircle circle) {
        circle.addRing(radius, color);
        circle.regenerateLayouts();
    }
}
