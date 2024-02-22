package cassunshine.thework.rendering.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Random;

public class RenderingUtilities {

    private static final Random renderRandom = new Random();

    private static MatrixStack stack;
    private static VertexConsumerProvider consumers;
    private static VertexConsumer consumer;

    private static int r, g, b, a;
    private static float normalX, normalY, normalZ;

    private static float wobbleAmount = 0;

    private static int light;
    private static int overlay;

    public static void setupStack(MatrixStack stack) {
        RenderingUtilities.stack = stack;
    }

    public static void setupConsumers(VertexConsumerProvider vertexConsumers) {
        RenderingUtilities.consumers = vertexConsumers;
    }

    public static void setupRenderLayer(RenderLayer layer) {
        RenderingUtilities.consumer = consumers.getBuffer(layer);
    }


    public static void setupLightOverlay(int light, int overlay) {
        RenderingUtilities.light = light;
        RenderingUtilities.overlay = overlay;
    }

    public static void setupColor(int red, int green, int blue, int alpha) {
        r = red;
        g = green;
        b = blue;
        a = alpha;
    }

    public static void setupNormal(float x, float y, float z) {
        normalX = x;
        normalY = y;
        normalZ = z;
    }

    public static void setupWobble(float wobble) {
        wobbleAmount = wobble;
    }

    public static void saneVertex(float x, float y, float z, float u, float v) {
        consumer.vertex(stack.peek().getPositionMatrix(), x + getWobble(), y, z + getWobble()).color(r, g, b, a).texture(u, v).overlay(overlay).light(light).normal(stack.peek().getNormalMatrix(), normalX, normalY, normalZ).next();
    }

    public static void saneVertex(double x, double y, double z, float u, float v) {
        saneVertex((float) x, (float) y, (float) z, u, v);
    }

    public static void renderItem(ItemStack stack, World world, int light, int overlay) {
        if (stack.isEmpty())
            return;
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, light, overlay, RenderingUtilities.stack, consumers, world, 0);
    }

    public static void renderBlock(BlockState state, int light, int overlay) {
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, stack, consumers, light, overlay);
    }


    public static void pushMat() {
        stack.push();
    }

    public static void popMat() {
        stack.pop();

    }

    public static void translateMatrix(float x, float y, float z) {
        stack.translate(x, y, z);
    }

    public static void translateMatrix(double x, double y, double z) {
        stack.translate(x, y, z);
    }


    public static void rotateMatrix(float x, float y, float z) {
        stack.multiply(new Quaternionf().rotationX(x));
        stack.multiply(new Quaternionf().rotationY(y));
        stack.multiply(new Quaternionf().rotationZ(z));
    }

    public static void scaleMatrix(float x, float y, float z) {
        stack.scale(x, y, z);
    }

    public static float getWobble() {
        if (wobbleAmount <= 0)
            return 0;
        return (renderRandom.nextFloat() - 0.5f) * wobbleAmount;
    }

}
