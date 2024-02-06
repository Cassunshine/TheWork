package cassunshine.thework.blockentities.alchemy_circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blockentities.alchemy_circle.rings.AlchemyRing;
import cassunshine.thework.entities.InteractionPointEntity;
import cassunshine.thework.entities.TheWorkEntities;
import cassunshine.thework.network.TheWorkNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;

public class AlchemyCircleBlockEntity extends BlockEntity {

    public final ArrayList<AlchemyRing> rings = new ArrayList<>();

    private final ArrayList<InteractionPointEntity> entities = new ArrayList<>();

    public boolean isOutward = true;

    public AlchemyCircleBlockEntity(BlockPos pos, BlockState state) {
        super(TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE, pos, state);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        AlchemyCircles.addCircle(this);
        TheWorkMod.schedule(this::regenerateInteractionPoints);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("outward", isOutward);

        NbtList ringsList = new NbtList();
        nbt.put("rings", ringsList);

        for (AlchemyRing ring : rings) {
            NbtCompound ringCompound = new NbtCompound();

            ring.writeNbt(ringCompound);
            ringsList.add(ringCompound);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        isOutward = nbt.getBoolean("outward");

        NbtList ringList = nbt.getList("rings", NbtElement.COMPOUND_TYPE);

        for (NbtElement element : ringList) {
            if (!(element instanceof NbtCompound ringCompound))
                continue;

            AlchemyRing ring = new AlchemyRing(this);
            ring.readNBT(ringCompound);

            rings.add(ring);
        }

        rings.sort(Comparator.comparingDouble(a -> a.radius));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void addRing(float radius) {
        //Try to remove a ring first.
        for (int i = 0; i < rings.size(); i++) {
            float difference = MathHelper.abs(radius - rings.get(i).radius);

            if (difference < 0.5f) {
                rings.remove(i);

                if (rings.isEmpty())
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                return;
            }
        }

        AlchemyRing ring = new AlchemyRing(this);
        ring.setRadius(radius);

        //Add ring and then sort rings by radius.
        rings.add(ring);
        rings.sort(Comparator.comparingDouble(a -> a.radius));
    }


    public boolean handleInteraction(ItemUsageContext context) {
        for (AlchemyRing ring : rings)
            if (ring.handleInteraction(context)) {
                markDirty();
                return true;
            }

        return false;
    }

    @Override
    public void markRemoved() {
        super.markRemoved();

        AlchemyCircles.removeCircle(this);

        for (InteractionPointEntity entity : entities)
            entity.remove(Entity.RemovalReason.DISCARDED);
    }

    private void regenerateInteractionPoints() {
        for (InteractionPointEntity entity : entities)
            entity.remove(Entity.RemovalReason.DISCARDED);
        entities.clear();

        for (AlchemyRing ring : rings)
            ring.regenerateInteractionPoints();
    }

    public Entity addInteractionPoint(Vec3d position) {
        if (world == null || world.isClient)
            return null;

        InteractionPointEntity interactionEntity = new InteractionPointEntity(TheWorkEntities.INTERACTION_POINT_TYPE, getWorld());
        interactionEntity.setPosition(position.add(0, 0, 0));

        entities.add(interactionEntity);
        world.spawnEntity(interactionEntity);

        interactionEntity.setCircle(this);

        return interactionEntity;
    }

    public void removeInteractionPoint(Entity entity) {
        entities.remove(entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
    }
}
