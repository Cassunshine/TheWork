package cassunshine.thework.items;

import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircles;
import cassunshine.thework.blocks.TheWorkBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;

public class ChalkItem extends Item {
    public ChalkItem() {
        super(
                new FabricItemSettings()
        );
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        NbtCompound stackNbt = context.getStack().getOrCreateNbt();

        if (stackNbt.contains("start_pos", NbtElement.INT_ARRAY_TYPE)) {
            var pos = stackNbt.getIntArray("start_pos");
            stackNbt.remove("start_pos");

            int x = pos[0];
            int y = pos[1];
            int z = pos[2];

            float dist = Vector3f.distance(x, y, z, context.getBlockPos().getX(), context.getBlockPos().getY(), context.getBlockPos().getZ());
            dist = Math.max(dist, 0.5f);

            if (dist > 64.0f)
                return ActionResult.SUCCESS;

            var usedBlock = context.getWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
            attemptDrawCircle(x, usedBlock == TheWorkBlocks.ALCHEMY_CIRCLE_BLOCK ? y : y + 1, z, dist, context.getWorld());
        } else {

            //Try to interact with alchemy circles in-world first.
            var interactionResult = AlchemyCircles.handleNearestInteraction(context);
            if (interactionResult)
                return ActionResult.SUCCESS;

            //If not, draw a new circle.
            int[] pos = new int[]{
                    context.getBlockPos().getX(),
                    context.getBlockPos().getY(),
                    context.getBlockPos().getZ(),
            };

            stackNbt.putIntArray("start_pos", pos);
        }

        return ActionResult.SUCCESS;
    }


    private void attemptDrawCircle(int x, int y, int z, float radius, World world) {
        float biasedRadius = radius < 1 ? radius : radius + 1;
        int intRadius = MathHelper.ceil(biasedRadius);

        ArrayList<BlockPos> blocksToRemove = new ArrayList<>();

        for (int ix = -intRadius; ix <= intRadius; ix++) {
            for (int iz = -intRadius; iz <= intRadius; iz++) {
                BlockPos actualPos = new BlockPos(x + ix, y, z + iz);
                float squareDistance = (float) actualPos.add(0, -actualPos.getY(), 0).getSquaredDistance(x, 0, z);

                if (squareDistance < (biasedRadius * biasedRadius) && !checkValidCirclePos(blocksToRemove, actualPos, world, squareDistance))
                    return;
            }
        }

        blocksToRemove.sort(Comparator.comparingDouble(a -> a.getSquaredDistance(x, y, z)));

        for (BlockPos pos : blocksToRemove)
            world.setBlockState(pos, Blocks.AIR.getDefaultState());

        BlockPos targetPos = new BlockPos(x, y, z);

        if (world.getBlockState(targetPos).getBlock() != TheWorkBlocks.ALCHEMY_CIRCLE_BLOCK)
            world.setBlockState(targetPos, TheWorkBlocks.ALCHEMY_CIRCLE_BLOCK.getDefaultState());

        var maybeBlockEntity = world.getBlockEntity(targetPos, TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE);
        if (maybeBlockEntity.isEmpty())
            return;

        AlchemyCircleBlockEntity blockEntity = maybeBlockEntity.get();

        blockEntity.addRing(radius);
    }


    private boolean checkValidCirclePos(ArrayList<BlockPos> toRemove, BlockPos pos, World world, float dist) {
        BlockPos basePos = pos.add(0, -1, 0);

        BlockState circleState = world.getBlockState(pos);
        BlockState baseState = world.getBlockState(basePos);

        if (!baseState.isFullCube(world, basePos))
            return false;

        if (circleState.getBlock() == Blocks.AIR || (dist < 0.01f && circleState.getBlock() == TheWorkBlocks.ALCHEMY_CIRCLE_BLOCK)) {
            //toRemove.add(pos);
            return true;
        }

        if (circleState.isReplaceable()) {
            toRemove.add(pos);
            return true;
        }

        return false;
    }
}
