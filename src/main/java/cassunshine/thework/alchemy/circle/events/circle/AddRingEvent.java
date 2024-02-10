package cassunshine.thework.alchemy.circle.events.circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AddRingEvent extends AlchemyCircleEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "circle_add_ring");

    public float radius;

    public AddRingEvent() {
        super(IDENTIFIER);
    }

    public AddRingEvent(float radius, AlchemyCircle circle) {
        super(circle, IDENTIFIER);

        this.radius = radius;
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeFloat(radius);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        radius = buf.readFloat();
    }

    @Override
    public void applyToCircle(AlchemyCircle circle) {
        circle.addRing(radius);
    }
}
