package cassunshine.thework.utils;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Function;

public class TheWorkUtils {

    public static void dropItem(World world, ItemStack stack, double x, double y, double z) {
        var newStack = stack.copyAndEmpty();
        if (world.isClient) return;

        var entity = new ItemEntity(world, x, y, z, newStack);
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

    public static <T> String generateSignature(T[] array, Function<T, String> signaturePer) {
        StringBuilder sb = new StringBuilder();

        for (T t : array) {
            sb.append('[');
            sb.append(signaturePer.apply(t));
            sb.append(']');
        }

        return sb.toString();
    }

    public static <T> String generateSignature(ArrayList<T> list, Function<T, String> signaturePer) {
        StringBuilder sb = new StringBuilder();

        for (T t : list) {
            sb.append('[');
            sb.append(signaturePer.apply(t));
            sb.append(']');
        }

        return sb.toString();
    }
}
