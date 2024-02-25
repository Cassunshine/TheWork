package cassunshine.thework.network.events;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class EntityEvent extends TheWorkNetworkEvent {
    public UUID entityId;

    public EntityEvent(Identifier id) {
        super(id);
    }

    public EntityEvent(UUID entityID, Identifier eventID) {
        super(eventID);

        this.entityId = entityID;
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeUuid(entityId);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        entityId = buf.readUuid();
    }
}
