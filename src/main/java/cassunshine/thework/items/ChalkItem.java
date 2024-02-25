package cassunshine.thework.items;

import cassunshine.thework.alchemy.circle.events.circle.AddRingEvent;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChalkItem extends Item {
    public final int color;

    public ChalkItem(int color) {
        super(new FabricItemSettings());

        this.color = color;
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return new ItemStack(this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var stackNbt = context.getStack().getOrCreateNbt();

        if (stackNbt.contains("last_circle", NbtElement.LONG_TYPE)) {
            //If we've previously interacted with a circle's center, we're drawing a new ring.
            var pos = BlockPos.fromLong(stackNbt.getLong("last_circle"));

            //If there is no alchemy circle at the given position, reset the item.
            if (!(context.getWorld().getBlockEntity(pos) instanceof AlchemyCircleBlockEntity entity)) {
                context.getStack().setNbt(new NbtCompound());
                return ActionResult.PASS;
            }

            //Calculate radius and add a new ring.
            float radius = ((float) context.getHitPos().withAxis(Direction.Axis.Y, 0).distanceTo(pos.toCenterPos().withAxis(Direction.Axis.Y, 0)));

            radius = Math.round(radius * 4.0f) / 4.0f;

            //Add ring to circle.
            TheWorkNetworkEvents.sendEvent(pos, context.getWorld(), new AddRingEvent(radius, color, entity.circle));

            context.getStack().getNbt().remove("last_circle");

            return ActionResult.SUCCESS;
        } else {
            //Try to interact with existing alchemy circle
            if (AlchemyCircleBlockEntity.generateAndSendEventNearest(context))
                return ActionResult.SUCCESS;

            //If there isn't a circle, place one.


            //Calculate where we're placing it, and if that block is replaceable or air.
            var realPos = context.getBlockPos().add(context.getSide().getVector());
            var realPosBlock = context.getWorld().getBlockState(realPos);
            if (!realPosBlock.isAir() && !realPosBlock.isReplaceable()) return ActionResult.PASS;

            //If alchemy circle can be placed there, place it, and interact with it.
            var state = TheWorkBlocks.ALCHEMY_CIRCLE_BLOCK.getDefaultState();
            if (state.canPlaceAt(context.getWorld(), realPos)) {
                context.getWorld().setBlockState(realPos, state);

                var hit = new BlockHitResult(context.getHitPos(), context.getSide(), realPos, context.hitsInsideBlock());

                AlchemyCircleBlockEntity.generateAndSendEventNearest(new ItemUsageContext(context.getPlayer(), context.getHand(), hit));
                return ActionResult.SUCCESS;
            }
        }

        //Default.
        return ActionResult.PASS;
    }
}
