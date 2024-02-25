package cassunshine.thework.blockentities.chemistry;

import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blocks.AlchemyJarBlock;
import cassunshine.thework.blocks.DistilleryBlock;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.utils.TheWorkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DistilleryBlockEntity extends BlockEntity {
    public ItemStack jarStack = ItemStack.EMPTY;
    public ItemStack fuelStack = ItemStack.EMPTY;

    public int fuel;

    public DistilleryBlockEntity(BlockPos pos, BlockState state) {
        super(TheWorkBlockEntities.DISTILLERY_TYPE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, DistilleryBlockEntity distilleryBlockEntity) {
        var facingPos = pos.add(state.get(DistilleryBlock.FACING).rotateYCounterclockwise().getVector());
        var facingState = world.getBlockState(facingPos);

        if (!facingState.isOf(TheWorkBlocks.ALCHEMY_JAR_BLOCK))
            return;

        var be = world.getBlockEntity(facingPos, TheWorkBlockEntities.ALCHEMY_JAR_TYPE);
        if (be.isEmpty())
            return;


        if (distilleryBlockEntity.fuel < 200 && !distilleryBlockEntity.fuelStack.isEmpty() && !distilleryBlockEntity.jarStack.isEmpty()) {
            var map = AbstractFurnaceBlockEntity.createFuelTimeMap();

            var burnAmount = map.get(distilleryBlockEntity.fuelStack.getItem());
            var points = burnAmount;

            distilleryBlockEntity.fuel += points;
            var remainder = distilleryBlockEntity.fuelStack.getItem().getRecipeRemainder(distilleryBlockEntity.fuelStack);
            var dropPos = pos.toCenterPos().add(0, 0.6f, 0);

            TheWorkUtils.dropItem(world, remainder, dropPos.x, dropPos.y, dropPos.z);

            distilleryBlockEntity.fuelStack.decrement(1);
        }

        if (distilleryBlockEntity.fuel > 0) {
            var stackInventory = AlchemyJarBlock.getInventoryForStack(distilleryBlockEntity.jarStack);

            if (world.random.nextFloat() > 0.0f) {
                world.addParticle(ParticleTypes.FLAME,
                        pos.getX() + world.random.nextFloat(), pos.getY() + 0.1f, pos.getZ() + world.random.nextFloat(),
                        (world.random.nextFloat() - 0.5f) * 0.01f, world.random.nextFloat() * 0.05f, (world.random.nextFloat() - 0.5f)  * 0.01f
                );
            }

            var element = Elements.getElement(be.get().element);

            if (element == Elements.NONE)
                element = Elements.getElement(new Identifier(distilleryBlockEntity.jarStack.getNbt().getString("element")));

            if (element == Elements.NONE)
                return;

            for (int i = 0; i < 10 && distilleryBlockEntity.fuel >= 200; i++) {
                if (be.get().amount < 2048 && stackInventory.has(element, 1)) {
                    stackInventory.take(element, 1);
                    be.get().element = element.id;
                    be.get().amount++;

                    be.get().markDirty();
                    distilleryBlockEntity.markDirty();

                    distilleryBlockEntity.fuel -= 200;
                }
            }
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var stack = player.getStackInHand(hand);

        if (stack.isEmpty()) {
            if (jarStack.isEmpty())
                player.equipStack(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, fuelStack.copyAndEmpty());
            else
                player.equipStack(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, jarStack.copyAndEmpty());

            return ActionResult.SUCCESS;
        }

        var item = stack.getItem();

        if (item instanceof BlockItem bi && bi.getBlock() instanceof AlchemyJarBlock alchemyJarBlock && jarStack.isEmpty()) {
            jarStack = stack.copyAndEmpty();
            return ActionResult.SUCCESS;
        }


        if (AbstractFurnaceBlockEntity.canUseAsFuel(stack) && fuelStack.isEmpty()) {
            fuelStack = stack.copyAndEmpty();
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        jarStack = ItemStack.fromNbt(nbt.getCompound("jar_stack"));
        fuelStack = ItemStack.fromNbt(nbt.getCompound("fuel_stack"));

        fuel = nbt.getInt("fuel");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("jar_stack", jarStack.writeNbt(new NbtCompound()));
        nbt.put("fuel_stack", fuelStack.writeNbt(new NbtCompound()));

        nbt.putInt("fuel", fuel);
    }
}
