package cassunshine.thework.blockentities.alchemy_circle.events.circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SetCircleOutwardEvent extends AlchemyCircleEvent {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "event/set_circle_outward");

    public boolean isOutward;

    public SetCircleOutwardEvent() {
        super(IDENTIFIER);
    }

    public SetCircleOutwardEvent(boolean clockwise, AlchemyCircleBlockEntity circle) {
        super(circle, IDENTIFIER);

        isOutward = clockwise;
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);

        buf.writeBoolean(isOutward);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        isOutward = buf.readBoolean();
    }

    @Override
    public void applyToCircle(AlchemyCircleBlockEntity circle) {
        circle.isOutward = isOutward;
    }
}
