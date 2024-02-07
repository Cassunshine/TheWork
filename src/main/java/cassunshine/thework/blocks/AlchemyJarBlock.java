package cassunshine.thework.blocks;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlchemyJarBlock extends BlockWithEntity {

    private final VoxelShape SHAPE = VoxelShapes.cuboid(0.3125f, 0, 0.3125f, 0.6875f, 0.625f, 0.6875f);

    protected AlchemyJarBlock() {
        super(FabricBlockSettings.create().breakInstantly().nonOpaque());
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    //TODO - Keep inventory
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return super.getDroppedStacks(state, builder);
    }

    /**
     * Checks if a given item stack is either empty, or an empty jar.
     */
    public static boolean isEmptyOrEmptyJar(ItemStack stack) {
        //If the stack is empty, it's empty, regardless.
        if (stack.isEmpty())
            return true;

        //If the item isn't a block item, it's not empty jar.
        if (!(stack.getItem() instanceof BlockItem bi))
            return false;

        //If the block isn't a jar, it's not an empty jar.
        if (!(bi.getBlock() instanceof AlchemyJarBlock jar))
            return false;

        //TODO - Check emptiness of jar.
        return true;
    }
}
