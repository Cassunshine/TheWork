package cassunshine.thework.blockentities.alchemycircle;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;

public class AlchemyCircleBlockEntity extends BlockEntity {

    /**
     * Hashmap of World -> List Of Alchemy Circle BE
     * <p>
     * Where each world stores a list of all alchemy circles within it.
     */
    private static final HashMap<World, ArrayList<AlchemyCircleBlockEntity>> LOADED = new HashMap<>();

    public final AlchemyCircle circle;

    public AlchemyCircleBlockEntity(BlockPos pos, BlockState state) {
        super(TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE, pos, state);

        circle = new AlchemyCircle(this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("circle", circle.writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        circle.readNbt(nbt.getCompound("circle"));
    }


    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        synchronized (LOADED) {
            LOADED.computeIfAbsent(world, id -> new ArrayList<>()).add(this);
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();

        synchronized (LOADED) {
            var list = LOADED.get(this.world);

            list.remove(this);
            if (list.isEmpty()) LOADED.remove(world);
        }
    }
}
