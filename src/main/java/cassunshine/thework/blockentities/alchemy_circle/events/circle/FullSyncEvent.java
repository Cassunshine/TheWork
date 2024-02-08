package cassunshine.thework.blockentities.alchemy_circle.events.circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class FullSyncEvent extends AlchemyCircleEvent {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "full_sync");

    private NbtCompound data;

    public FullSyncEvent(Identifier id) {
        super(id);
    }

    public FullSyncEvent(AlchemyCircleBlockEntity circle, Identifier id) {
        super(circle, id);
    }

    public FullSyncEvent() {
        this(IDENTIFIER);
    }

    public FullSyncEvent(AlchemyCircleBlockEntity circle) {
        this(circle, IDENTIFIER);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeNbt(data);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        data = buf.readNbt();
    }

    @Override
    public void applyToCircle(AlchemyCircleBlockEntity target) {

        //Does nothing to the server, only used by server to force clients to sync.
        if (target.getWorld().isClient)
            target.readNbt(data);
    }
}
