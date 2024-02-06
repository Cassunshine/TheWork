package cassunshine.thework.blockentities.jar;

import cassunshine.thework.blockentities.TheWorkBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class AlchemyJarBlockEntity extends BlockEntity {


    public AlchemyJarBlockEntity(BlockPos pos, BlockState state) {
        super(TheWorkBlockEntities.ALCHEMY_JAR_TYPE, pos, state);
    }
}
