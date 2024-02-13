package cassunshine.thework.blocks;

import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blockentities.chemistry.ChemistrySetBlockEntity;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChemistrySetBlock extends BlockWithEntity {

    public static final VoxelShape DEFAULT_SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, 1, 1);

    protected ChemistrySetBlock() {
        super(FabricBlockSettings.create().nonOpaque());
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChemistrySetBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BlockWithEntity.validateTicker(type, TheWorkBlockEntities.CHEMISTRY_SET_TYPE, (w, p, s, b) -> b.tick());
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        BlockEntity be = (BlockEntity) builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if (be instanceof ChemistrySetBlockEntity chemistrySet) {
            //TODO - Drop chemistry set.
        }

        return super.getDroppedStacks(state, builder);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DEFAULT_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity be = world.getBlockEntity(pos);

        if (!(be instanceof ChemistrySetBlockEntity chemistrySet))
            return DEFAULT_SHAPE;

        return chemistrySet.currentShape;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);

        if (!(be instanceof ChemistrySetBlockEntity chemistrySet))
            return ActionResult.PASS;

        return chemistrySet.onUse(player, hand, hit);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        var belowPos = pos.add(0, -1, 0);
        return world.getBlockState(belowPos).isFullCube(world, belowPos);
    }
}
