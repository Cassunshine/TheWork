package cassunshine.thework.blockentities.alchemy_circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.TheWorkItems;
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

    public static boolean handleInteraction(ItemUsageContext context) {
        //Pass chalk events to itself.
        if(context.getStack().getItem() == TheWorkItems.CHALK_ITEM) return false;


        //Try to interact with the nearest alchemy circle.
        var maybeCircle = AlchemyCircles.searchForNearestHorizontal(context.getBlockPos().add(context.getSide().getVector()), context.getWorld());
        if (maybeCircle != null)
            if (maybeCircle.handleInteraction(context))
                return true;

        return false;
    }

}
