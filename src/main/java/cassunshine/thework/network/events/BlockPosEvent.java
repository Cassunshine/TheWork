package cassunshine.thework.network.events;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BlockPosEvent extends TheWorkNetworkEvent {
    public BlockPos position;

    public BlockPosEvent(Identifier id) {
        super(id);
    }

    public BlockPosEvent(BlockPos pos, Identifier id) {
        super(id);

        position = pos;
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeBlockPos(position);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        position = buf.readBlockPos();
    }
}
