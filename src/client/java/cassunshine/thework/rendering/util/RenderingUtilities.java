package cassunshine.thework.rendering.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
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

    public static RenderingSpace SPACE;

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

    public static void setupColor(int color) {
        r = ColorHelper.Argb.getRed(color);
        g = ColorHelper.Argb.getGreen(color);
        b = ColorHelper.Argb.getBlue(color);
        a = 255;
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
        consumer.vertex(stack.peek().getPositionMatrix(), x + getWobble(), y, z + getWobble()).color(r, g, b, a).texture(u, v).overlay(overlay).light(light);

        if (normalX == 0 && normalY == 0 && normalZ == 0)
            consumer.normal(0, 1, 0);
        else
            consumer.normal(stack.peek().getNormalMatrix(), normalX, normalY, normalZ);

        consumer.next();
    }

    public static void saneVertexNoNormal(float x, float y, float z, float u, float v) {
        consumer.vertex(stack.peek().getPositionMatrix(), x + getWobble(), y, z + getWobble()).color(r, g, b, a).texture(u, v).overlay(overlay).light(light).normal(normalX, normalY, normalZ).next();
    }

    public static void saneVertex(double x, double y, double z, float u, float v) {
        saneVertex((float) x, (float) y, (float) z, u, v);
    }

    public static void renderItem(ItemStack stack, World world) {
        renderItem(stack, ModelTransformationMode.GROUND, world);
    }

    public static void renderItem(ItemStack stack, ModelTransformationMode mode, World world) {
        if (stack.isEmpty())
            return;
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, mode, light, overlay, RenderingUtilities.stack, consumers, world, 0);
    }


    public static void renderBlock(BlockState state, int light, int overlay) {
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, stack, consumers, light, overlay);
    }


    public static MatrixStack getMatStack() {
        return stack;
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

    public static void drawText(Text text, boolean withShadow) {
        var renderer = MinecraftClient.getInstance().textRenderer;

        renderer.draw(text, 0, 0, ColorHelper.Argb.getArgb(a, r, g, b), withShadow, stack.peek().getPositionMatrix(), consumers, TextRenderer.TextLayerType.NORMAL, 0xFFFFFFF, light);
    }

    public static void drawTextCentered(Text text, boolean withShadow) {
        var renderer = MinecraftClient.getInstance().textRenderer;
        var width = renderer.getWidth(text);

        renderer.draw(text, -width / 2.0f, 0, ColorHelper.Argb.getArgb(a, r, g, b), withShadow, stack.peek().getPositionMatrix(), consumers, TextRenderer.TextLayerType.NORMAL, 0xFFFFFFF, light);
    }

    public static void drawText(TextWidget widget, boolean withShadow) {

        var renderer = MinecraftClient.getInstance().textRenderer;
        var width = widget.getWidth();

        Text text = widget.getMessage();
        renderer.draw(text, widget.getX(), widget.getY(), ColorHelper.Argb.getArgb(a, r, g, b), withShadow, stack.peek().getPositionMatrix(), consumers, TextRenderer.TextLayerType.NORMAL, 0xFFFFFFF, light);
    }

    public enum RenderingSpace {
        WORLD,
        GUI
    }
}
