package cassunshine.thework.blockentities.alchemycircle;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.entities.InteractionPointEntity;
import cassunshine.thework.items.ChalkItem;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class AlchemyCircleBlockEntity extends BlockEntity {

    /**
     * Hashmap of World -> List Of Alchemy Circle BE
     * <p>
     * Where each world stores a list of all alchemy circles within it.
     */
    private static final HashMap<World, ArrayList<AlchemyCircleBlockEntity>> LOADED = new HashMap<>();

    private final ArrayList<InteractionPointEntity> interactionPoints = new ArrayList<>();
    private final ArrayList<InteractionPointEntity> newPoints = new ArrayList<>();


    public final Vec3d flatPosition;

    public final AlchemyCircle circle;

    public AlchemyCircleBlockEntity(BlockPos pos, BlockState state) {
        super(TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE, pos, state);

        circle = new AlchemyCircle(this);

        flatPosition = new Vec3d(pos.getX() + 0.5f, 0, pos.getZ() + 0.5f);
    }


    public static void tick(World world, BlockPos pos, BlockState blockState, AlchemyCircleBlockEntity blockEntity) {

        //Update new block entities.
        for (InteractionPointEntity newPoint : blockEntity.newPoints)
            newPoint.setCircle(blockEntity);
        blockEntity.newPoints.clear();

        if (!blockEntity.circle.isActive)
            return;

        var circle = blockEntity.circle;

        circle.activeTick();
    }

    public void regenerateInteractionPoints() {
        //Clear all previous interaction points.
        for (InteractionPointEntity point : interactionPoints)
            point.remove(Entity.RemovalReason.DISCARDED);
        interactionPoints.clear();

        //Generate entirely new ones.
        circle.regenerateInteractionPoints(this);
    }

    public InteractionPointEntity addInteractionPoint(Vec3d position) {
        if (getWorld() == null)
            return null;

        var point = new InteractionPointEntity(null, getWorld());
        point.setPosition(position);

        interactionPoints.add(point);
        getWorld().spawnEntity(point);

        newPoints.add(point);

        return point;
    }

    public InteractionPointEntity removeInteractionPoint(InteractionPointEntity toRemove) {
        if (interactionPoints.remove(toRemove))
            toRemove.remove(Entity.RemovalReason.DISCARDED);

        return null;
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
    public NbtCompound toInitialChunkDataNbt() {
        var cmp = new NbtCompound();
        writeNbt(cmp);
        return cmp;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        synchronized (LOADED) {
            LOADED.computeIfAbsent(world, id -> new ArrayList<>()).add(this);
        }

        regenerateInteractionPoints();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();

        synchronized (LOADED) {
            var list = LOADED.get(this.world);

            list.remove(this);
            if (list.isEmpty()) LOADED.remove(world);
        }

        for (InteractionPointEntity point : interactionPoints)
            point.remove(Entity.RemovalReason.DISCARDED);
        interactionPoints.clear();
    }

    //Handles interaction with the alchemy circle given a context.
    public static boolean generateAndSendEvent(AlchemyCircleBlockEntity entity, ItemUsageContext context) {
        var event = entity.generateEvent(context);
        return sendCircleEvent(entity, event);
    }

    public static boolean sendCircleEvent(AlchemyCircleBlockEntity entity, TheWorkNetworkEvent event) {
        if (event == TheWorkNetworkEvents.SUCCESS) {
            //Do nothing, but return as if we did so the item plays the swing animation.
            return true;
        }

        TheWorkNetworkEvents.sendEvent(entity.pos, entity.getWorld(), event);
        return event != TheWorkNetworkEvents.NONE;
    }

    //Handles interaction on the nearest alchemy circle.
    public static boolean generateAndSendEventNearest(ItemUsageContext context) {
        synchronized (LOADED) {
            var list = LOADED.get(context.getWorld());

            if (list == null)
                return false;

            AlchemyCircleBlockEntity nearestEntity = null;
            double nearestDistance = Float.POSITIVE_INFINITY;

            for (AlchemyCircleBlockEntity entity : list) {
                double dist = context.getBlockPos().getSquaredDistance(entity.pos);

                if (dist < nearestDistance) {
                    nearestDistance = dist;
                    nearestEntity = entity;
                }
            }

            return nearestEntity != null && generateAndSendEvent(nearestEntity, context);
        }
    }

    public static AlchemyCircleBlockEntity getNearestOnSameLevel(World world, BlockPos pos) {
        synchronized (LOADED) {
            var list = LOADED.get(world);

            if (list == null)
                return null;

            AlchemyCircleBlockEntity nearestEntity = null;
            double nearestDistance = Double.POSITIVE_INFINITY;

            for (AlchemyCircleBlockEntity entity : list) {
                if (entity.getPos().getY() != pos.getY())
                    continue;

                var newDist = entity.getPos().getSquaredDistance(pos);

                if (newDist < nearestDistance) {
                    nearestDistance = newDist;
                    nearestEntity = entity;
                }
            }

            return nearestEntity;
        }
    }

    public static void getAllMatching(World world, ArrayList<AlchemyCircleBlockEntity> output, Predicate<AlchemyCircleBlockEntity> predicate) {
        output.clear();

        synchronized (LOADED) {
            var list = LOADED.get(world);

            if (list == null)
                return;

            for (AlchemyCircleBlockEntity entity : list)
                if (predicate.test(entity))
                    output.add(entity);
        }
    }

    private TheWorkNetworkEvent generateEvent(ItemUsageContext context) {
        if (context.getStack().getItem() instanceof ChalkItem)
            return generateChalkEvent(context);

        return generateInteractEvent(context);
    }

    private TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context) {
        return circle.generateChalkEvent(context);
    }

    private TheWorkNetworkEvent generateInteractEvent(ItemUsageContext context) {
        return circle.generateInteractEvent(context);
    }
}
