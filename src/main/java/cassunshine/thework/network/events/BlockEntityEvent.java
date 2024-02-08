package cassunshine.thework.network.events;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockEntityEvent extends TheWorkNetworkEvent {
    public BlockPos targetPosition;

    public BlockEntityEvent(Identifier id) {
        super(id);
    }

    public BlockEntityEvent(BlockPos pos, Identifier id) {
        super(id);

        targetPosition = pos;
    }

    @Override
    public void applyToWorld(World world) {
        var be = world.getBlockEntity(targetPosition);

        if (be == null) return;

        applyToBlockEntity(be);
        be.markDirty();
    }

    public void applyToBlockEntity(BlockEntity target) {

    }
}
