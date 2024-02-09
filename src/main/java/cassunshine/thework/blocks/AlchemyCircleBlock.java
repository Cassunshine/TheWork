package cassunshine.thework.blocks;

import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AlchemyCircleBlock extends BlockWithEntity {
    private static final MapCodec<AlchemyCircleBlock> CODEC = createCodec(AlchemyCircleBlock::new);

    private final VoxelShape alchemyCircleCenterShape;

    protected AlchemyCircleBlock(Settings settings) {
        this();
    }

    protected AlchemyCircleBlock() {
        super(
                FabricBlockSettings.create()
                        .breakInstantly()
                        .dropsNothing()
                        .noCollision()
                        .nonOpaque()
                        .notSolid()
                        .noCollision()
                        .pistonBehavior(PistonBehavior.DESTROY)
                        .allowsSpawning(Blocks::never)
                        .solidBlock(Blocks::never)
                        .blockVision(Blocks::never)
                        .sounds(BlockSoundGroup.STONE)
        );

        alchemyCircleCenterShape = VoxelShapes.cuboid(0, 0, 0, 1, 1.0f / 16.0f, 1);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemyCircleBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE, AlchemyCircleBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var blockEntity = world.getBlockEntity(pos, TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE);

        if (blockEntity.isEmpty())
            return alchemyCircleCenterShape;

        return alchemyCircleCenterShape;
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var maybeBE = world.getBlockEntity(pos, TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE);

        if (maybeBE.isEmpty())
            return ActionResult.PASS;

        var blockEntity = maybeBE.get();


        if (blockEntity.isActive) {
            blockEntity.stop();
            return ActionResult.SUCCESS;
        }

        if (!blockEntity.validityCheck())
            return ActionResult.PASS;

        blockEntity.activate();
        return ActionResult.SUCCESS;
    }

}
