package cassunshine.thework.blocks;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.inventory.ElementInventory;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blockentities.jar.AlchemyJarBlockEntity;
import cassunshine.thework.items.TheWorkItems;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlchemyJarBlock extends BlockWithEntity {

    public static final VoxelShape SHAPE = VoxelShapes.cuboid(3 / 16.0f, 0, 3 / 16.0f, 13 / 16.0f, 12 / 16.0f, 13 / 16.0f);

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
        return new AlchemyJarBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    //TODO - Fix this because vanilla loot tables :(
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        var def = super.getDroppedStacks(state, builder);

        var stack = new ItemStack(BlockItem.BLOCK_ITEMS.get(this));
        def.add(stack);
        stack.setNbt(builder.get(LootContextParameters.BLOCK_ENTITY).createNbt());
        return def;
    }

    @Override
    public Item asItem() {
        return super.asItem();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        world.getBlockEntity(pos, TheWorkBlockEntities.ALCHEMY_JAR_TYPE).get().readNbt(itemStack.getNbt());
        world.getBlockEntity(pos).markDirty();
    }

    public static ElementInventory getInventoryForStack(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem bi) || bi.getBlock() != TheWorkBlocks.ALCHEMY_JAR_BLOCK)
            return null;

        return new JarInventory(stack);
    }

    private static class JarInventory extends ElementInventory {
        private final ItemStack stack;

        public JarInventory(ItemStack stack) {
            this.stack = stack;
            capacity = 2048;
        }

        @Override
        public float get(Element element) {
            if (!stack.hasNbt())
                return 0;

            var nbt = stack.getOrCreateNbt();
            if (!nbt.getString("element").equals(element.id.toString()))
                return 0;

            return nbt.getFloat("amount");
        }

        @Override
        public boolean canFit(Element element, float amount) {
            if (!stack.hasNbt())
                return super.canFit(element, amount);

            var nbt = stack.getOrCreateNbt();
            return nbt.contains("element", NbtElement.STRING_TYPE) && nbt.getString("element").equals(element.id.toString());
        }

        @Override
        protected void setAmount(Element element, float amount) {
            var nbt = stack.getOrCreateNbt();

            if (nbt.contains("element", NbtElement.STRING_TYPE) && !nbt.getString("element").equals(element.id.toString()))
                return;

            if (amount == 0) {
                nbt.remove("element");
                nbt.remove("amount");
            } else {
                nbt.putString("element", element.id.toString());
                nbt.putFloat("amount", amount);
            }
        }
    }
}
