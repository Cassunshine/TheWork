package cassunshine.thework.util;

import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import net.minecraft.util.collection.ArrayListDeque;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Optional;

public class AlchemyCircleUtils {

    public static Optional<AlchemyCircleBlockEntity> alchemyCircleSearch(BlockPos pos, World world) {
        return alchemyCircleSearch(pos, world, 64);
    }

    private static final BlockPos[] nPos = new BlockPos[]{
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
    };

    public static Optional<AlchemyCircleBlockEntity> alchemyCircleSearch(BlockPos pos, World world, int maxRadius) {
        ArrayListDeque<BlockPos> blockPosQueue = new ArrayListDeque<>();
        HashSet<BlockPos> searchedPositions = new HashSet<>();

        blockPosQueue.add(pos);
        searchedPositions.add(pos);

        while (!blockPosQueue.isEmpty()) {

            if(blockPosQueue.size() > 4096)
                return Optional.empty();

            BlockPos next = blockPosQueue.removeFirst();
            BlockPos belowPos = next.add(0, -1, 0);

            var belowState = world.getBlockState(belowPos);

            if(!belowState.isFullCube(world, belowPos))
                continue;

            var mbe = world.getBlockEntity(next, TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE);

            if (mbe.isPresent())
                return mbe;

            for (BlockPos np : nPos) {
                BlockPos total = np.add(next);

                if(total.getSquaredDistance(pos) > maxRadius * maxRadius)
                    continue;

                if (!searchedPositions.contains(total)) {
                    searchedPositions.add(total);
                    blockPosQueue.add(total);
                }
            }
        }

        return Optional.empty();
    }

}
