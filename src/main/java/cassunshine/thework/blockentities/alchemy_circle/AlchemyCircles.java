package cassunshine.thework.blockentities.alchemy_circle;

import cassunshine.thework.blockentities.alchemy_circle.events.circle.AlchemyCircleEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

/**
 * Used to keep track of, and search for, alchemy circles in the world.
 */
public class AlchemyCircles {
    public static final HashMap<World, ArrayList<AlchemyCircleBlockEntity>> CIRCLES = new HashMap<>();

    private static ArrayList<AlchemyCircleBlockEntity> getWorldMap(World world) {
        return CIRCLES.computeIfAbsent(world, w -> new ArrayList<>());
    }

    public synchronized static void addCircle(AlchemyCircleBlockEntity circle) {
        getWorldMap(circle.getWorld()).add(circle);
    }

    public synchronized static void removeCircle(AlchemyCircleBlockEntity circle) {
        var list = getWorldMap(circle.getWorld());
        list.remove(circle);

        if (list.isEmpty())
            CIRCLES.remove(circle.getWorld());
    }

    public synchronized static AlchemyCircleBlockEntity searchForNearest(BlockPos pos, World world) {

        AlchemyCircleBlockEntity nearest = null;
        float nearestDistance = Float.POSITIVE_INFINITY;
        var circles = CIRCLES.get(world);
        if (circles == null)
            return null;

        for (AlchemyCircleBlockEntity circle : circles) {
            float distSqr = (float) circle.getPos().getSquaredDistance(pos);

            if (distSqr < nearestDistance) {
                nearestDistance = distSqr;
                nearest = circle;
            }
        }

        return nearest;
    }

    public synchronized static AlchemyCircleBlockEntity searchForNearestHorizontal(BlockPos pos, World world) {

        AlchemyCircleBlockEntity nearest = null;
        float nearestDistance = Float.POSITIVE_INFINITY;
        var circles = CIRCLES.get(world);
        if (circles == null)
            return null;

        for (AlchemyCircleBlockEntity circle : circles) {
            if (circle.getPos().getY() != pos.getY())
                continue;

            float distSqr = (float) circle.getPos().getSquaredDistance(pos);

            if (distSqr < nearestDistance) {
                nearestDistance = distSqr;
                nearest = circle;
            }
        }

        return nearest;
    }

    public synchronized static void searchByCondition(World world, ArrayList<AlchemyCircleBlockEntity> target, Predicate<AlchemyCircleBlockEntity> predicate) {
        var circles = CIRCLES.get(world);
        if (circles == null)
            return;

        for (AlchemyCircleBlockEntity circle : circles)
            if (predicate.test(circle))
                target.add(circle);
    }

    public static boolean handleNearestInteraction(ItemUsageContext context) {
        var nearestCircle = searchForNearestHorizontal(context.getBlockPos().add(0, 1, 0), context.getWorld());

        if (nearestCircle == null)
            return false;

        return generateAndSendEvent(nearestCircle, context);
    }

    public static boolean generateAndSendEvent(AlchemyCircleBlockEntity alchemyCircle, ItemUsageContext context) {
        var event = alchemyCircle.generateInteractionEvent(context);

        //Client generates no events.
        if (context.getWorld().isClient)
            return event != TheWorkNetworkEvents.NONE;

        //Send alchemy circle events to players observing the circle.
        TheWorkNetworkEvents.sendEvent(alchemyCircle.getPos(), alchemyCircle.getWorld(), event);
        return event != TheWorkNetworkEvents.NONE;
    }

}
