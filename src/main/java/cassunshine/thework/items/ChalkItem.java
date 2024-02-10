package cassunshine.thework.items;

import cassunshine.thework.alchemy.circle.events.circle.AddRingEvent;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ChalkItem extends Item {
    public ChalkItem() {
        super(new FabricItemSettings());
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
            float radius = MathHelper.sqrt((float) context.getBlockPos().withY(0).getSquaredDistance(pos.withY(0)));

            //Add ring to circle.
            TheWorkNetworkEvents.sendEvent(pos, context.getWorld(), new AddRingEvent(radius, entity.circle));

            context.getStack().getNbt().remove("last_circle");

            return ActionResult.SUCCESS;
        } else {
            //Try to place new alchemy circle block.

            if (AlchemyCircleBlockEntity.generateAndSendEventNearest(context))
                return ActionResult.SUCCESS;

            //Calculate where we're placing it, and if that block is replaceable or air.
            var realPos = context.getBlockPos().add(context.getSide().getVector());
            var realPosBlock = context.getWorld().getBlockState(realPos);
            if (!realPosBlock.isAir() && !realPosBlock.isReplaceable()) return ActionResult.PASS;

            //If alchemy circle can be placed there, place it.
            var state = TheWorkBlocks.ALCHEMY_CIRCLE_BLOCK.getDefaultState();
            if (state.canPlaceAt(context.getWorld(), realPos)) {
                context.getWorld().setBlockState(realPos, state);
                return ActionResult.SUCCESS;
            }
        }

        //Default.
        return ActionResult.PASS;
    }
}
