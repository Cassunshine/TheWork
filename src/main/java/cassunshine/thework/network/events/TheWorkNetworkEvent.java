package cassunshine.thework.network.events;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TheWorkNetworkEvent {
    public final Identifier id;
    public BlockPos position;

    public TheWorkNetworkEvent(Identifier id) {
        this.id = id;
    }

    public TheWorkNetworkEvent(BlockPos pos, Identifier id) {
        this(id);
        position = pos;
    }

    public void writePacket(PacketByteBuf buf) {
        buf.writeBlockPos(position);
    }

    public void readPacket(PacketByteBuf buf) {
        position = buf.readBlockPos();
    }

    public void applyToWorld(World world) {

    }
}
