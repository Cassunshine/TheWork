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

public class RenderingUtilities {
    private static MatrixStack stack;
    private static VertexConsumerProvider consumers;
    private static VertexConsumer consumer;

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

    public static void saneVertex(float x, float y, float z, int red, int green, int blue, float u, float v, float normalX, float normalY, float normalZ) {
        consumer.vertex(stack.peek().getPositionMatrix(), x, y, z).color(red, green, blue, 255).texture(u, v).overlay(overlay).light(light).normal(stack.peek().getNormalMatrix(), normalX, normalY, normalZ).next();
    }

    public static void renderItem(ItemStack stack, World world, int light, int overlay) {
        if(stack.isEmpty())
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

    public static void rotateMatrix(float x, float y, float z) {
        stack.multiply(new Quaternionf().rotationX(x));
        stack.multiply(new Quaternionf().rotationY(y));
        stack.multiply(new Quaternionf().rotationZ(z));
    }

}
