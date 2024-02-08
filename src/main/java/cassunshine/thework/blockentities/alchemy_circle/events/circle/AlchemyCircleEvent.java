package cassunshine.thework.blockentities.alchemy_circle.events.circle;

import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import cassunshine.thework.blocks.AlchemyCircleBlock;
import cassunshine.thework.network.events.BlockEntityEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class AlchemyCircleEvent extends BlockEntityEvent {

    public AlchemyCircleEvent(Identifier id) {
        super(id);
    }

    public AlchemyCircleEvent(AlchemyCircleBlockEntity circle, Identifier id) {
        super(circle.getPos(), id);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(targetPosition);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        targetPosition = buf.readBlockPos();
    }

    @Override
    public void applyToBlockEntity(BlockEntity target) {
        if (!(target instanceof AlchemyCircleBlockEntity acbe))
            return;

        applyToCircle(acbe);
    }

    public void applyToCircle(AlchemyCircleBlockEntity target) {

    }
}
