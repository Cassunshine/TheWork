package cassunshine.thework.blockentities.alchemycircle;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.network.events.BlockEntityEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;

public class AlchemyCircleBlockEntityEvent extends BlockEntityEvent {
    public AlchemyCircleBlockEntityEvent(Identifier id) {
        super(id);
    }

    public AlchemyCircleBlockEntityEvent(AlchemyCircleBlockEntity blockEntity, Identifier id) {
        super(blockEntity.getPos(), id);
    }

    @Override
    public void applyToBlockEntity(BlockEntity target) {
        if (!(target instanceof AlchemyCircleBlockEntity alchemyCircleBlockEntity)) return;

        applyToCircle(alchemyCircleBlockEntity.circle);
    }


    public void applyToCircle(AlchemyCircle circle) {

    }
}
