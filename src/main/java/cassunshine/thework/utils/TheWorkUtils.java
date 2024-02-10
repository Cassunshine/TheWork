package cassunshine.thework.utils;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TheWorkUtils {

    public static void dropItem(World world, ItemStack stack, float x, float y, float z) {
        var newStack = stack.copyAndEmpty();
        if (world.isClient) return;

        var entity = new ItemEntity(world, x, y, z, stack);
        world.spawnEntity(entity);
    }


    public static float wrapRadians(float radians) {
        float f = radians % MathHelper.TAU;
        if (f >= MathHelper.PI) {
            f -= MathHelper.TAU;
        }

        if (f < -MathHelper.PI) {
            f += MathHelper.TAU;
        }

        return f;
    }

    public static float lerpRadians(float delta, float start, float end) {
        return start + delta * wrapRadians(end - start);
    }

    public static float subtractAngles(float a, float b) {
        return wrapRadians(a - b);
    }

    public static float angleBetweenRadians(float a, float b) {
        return MathHelper.abs(subtractAngles(a, b));
    }

}
