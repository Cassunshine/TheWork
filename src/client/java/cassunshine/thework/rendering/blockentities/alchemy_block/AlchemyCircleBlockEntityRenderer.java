package cassunshine.thework.rendering.blockentities.alchemy_block;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeType;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.rendering.blockentities.alchemy_block.nodes.AlchemyNodeTypeRenderers;
import cassunshine.thework.rendering.util.RenderingUtilities;
import cassunshine.thework.utils.TheWorkUtils;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.injection.selectors.ElementNode;

import java.util.ArrayList;
import java.util.Random;

public class AlchemyCircleBlockEntityRenderer implements BlockEntityRenderer<AlchemyCircleBlockEntity> {

    public static final Random renderRandom = new Random();

    private static final float LINE_THICKNESS = 1 / 16.0f;
    private static final float HALF_LINE_THICKNESS = LINE_THICKNESS / 2.0f;

    private static final Identifier ALCHEMY_CIRCLE_TEXTURE = new Identifier(TheWorkMod.ModID, "textures/other/alchemy_circle.png");

    private ArrayList<AlchemyNode> specialRenders = new ArrayList<>();

    public AlchemyCircleBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public int getRenderDistance() {
        return 1024;
    }

    @Override
    public boolean rendersOutsideBoundingBox(AlchemyCircleBlockEntity blockEntity) {
        return true;
    }

    public RenderLayer getLayer(AlchemyCircle circle) {
        return circle.isActive ? RenderLayer.getEntityTranslucentEmissive(ALCHEMY_CIRCLE_TEXTURE) : RenderLayer.getEntityCutoutNoCull(ALCHEMY_CIRCLE_TEXTURE);
    }

    @Override
    public void render(AlchemyCircleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var circle = entity.circle;

        RenderingUtilities.setupStack(matrices);

        RenderingUtilities.pushMat();
        RenderingUtilities.setupConsumers(vertexConsumers);
        RenderingUtilities.setupRenderLayer(getLayer(circle));

        RenderingUtilities.setupLightOverlay(light, overlay);
        RenderingUtilities.setupNormal(0, 1, 0);
        RenderingUtilities.setupColor(255, 255, 255, 255);

        renderRandom.setSeed(entity.getPos().asLong());

        try {
            //Offset to middle of block, and move up a little to prevent z-fighting.
            RenderingUtilities.translateMatrix(0.5f, 1 / 32.0f, 0.5f);

            drawFullCircle(0.5f, 8);
            drawFullCircle(0.5f, 4);

            drawCirclePips(0.5f + LINE_THICKNESS, 0, MathHelper.PI, 4, 1 / 8.0f, false);
            drawCirclePips(0.5f + LINE_THICKNESS, MathHelper.PI, MathHelper.TAU, 4, 1 / 8.0f, false);

            for (int i = 0; i < circle.rings.size(); i++) {
                var ring = circle.rings.get(i);
                var nextRing = circle.isOutward ?
                        (i == circle.rings.size() - 1 ? null : circle.rings.get(i + 1)) :
                        (i == 0 ? null : circle.rings.get(i - 1));

                drawRing(ring, nextRing);
            }

            for (AlchemyNode node : specialRenders) {
                drawNode(node);
            }
            specialRenders.clear();

        } catch (Exception e) {
            TheWorkMod.LOGGER.error(e.toString());
        }

        RenderingUtilities.popMat();
    }


    private void drawRing(AlchemyRing ring, AlchemyRing next) {

        var clockwise = ring.isClockwise;
        var nodeWidthAngle = MathHelper.lerp(0.5f / ring.circumference, 0, MathHelper.TAU);
        var spinMult = clockwise ? 1 : -1;

        for (int i = 0; i < ring.nodes.length; i++) {

            var indexNext = ring.getNextNodeIndex(i);

            var nodeThis = ring.getNode(i);
            var nextNode = ring.getNode(indexNext);

            var progressThis = nodeThis.index / (float) ring.nodes.length;
            var angleThis = progressThis * MathHelper.TAU;
            angleThis = TheWorkUtils.wrapRadians(angleThis);

            var progressNext = nextNode.index / (float) ring.nodes.length;
            var angleNext = progressNext * MathHelper.TAU;
            angleNext = TheWorkUtils.wrapRadians(angleNext);

            if (nodeThis.nodeType != AlchemyNodeTypes.NONE)
                specialRenders.add(nodeThis);

            if (nodeThis.nodeType != AlchemyNodeTypes.NONE)
                angleThis -= nodeWidthAngle * spinMult;
            if (nextNode.nodeType != AlchemyNodeTypes.NONE)
                angleNext += nodeWidthAngle * spinMult;


            int length = MathHelper.ceil(TheWorkUtils.angleBetweenRadians(progressNext * MathHelper.TAU, progressThis * MathHelper.TAU) * ring.radius);

            drawPippedCircleSegment(ring.radius, angleThis, angleNext, length, length * 3, ring.isClockwise ? LINE_THICKNESS : -LINE_THICKNESS * 3, ring.isClockwise ? 0.1f : -0.05f);

        }
    }

    private void drawNode(AlchemyNode node) {
        RenderingUtilities.pushMat();

        try {
            RenderingUtilities.translateMatrix(MathHelper.sin(node.getAngle()) * node.ring.radius, 0, MathHelper.cos(node.getAngle()) * node.ring.radius);
            RenderingUtilities.rotateMatrix(0, node.getAngle() + MathHelper.PI, 0);

            //TODO - Check perf on this
            RenderingUtilities.setupRenderLayer(getLayer(node.ring.circle));
            drawFullCircle(0.5f, 8);

            //Render item
            if (!node.heldStack.isEmpty())
                RenderingUtilities.renderItem(node.heldStack, node.ring.circle.blockEntity.getWorld(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);

            //Run custom renderer
            var customRenderer = AlchemyNodeTypeRenderers.get(node.nodeType);
            if (customRenderer != null)
                customRenderer.render(node);

        } catch (Exception e) {
            TheWorkMod.LOGGER.error(e.toString());
        }
        RenderingUtilities.popMat();
    }

    private void drawFullCircle(float radius, int segments) {
        float circleMin = radius;
        float circleMax = radius - LINE_THICKNESS;

        for (int i = 0; i < segments; i++) {
            float progressThis = i / (float) segments;
            float angleThis = MathHelper.lerp(progressThis, 0, MathHelper.TAU);
            float sinThis = MathHelper.sin(angleThis);
            float cosThis = MathHelper.cos(angleThis);

            float progressNext = (i + 1) / (float) segments;
            float angleNext = MathHelper.lerp(progressNext, 0, MathHelper.TAU);
            float sinNext = MathHelper.sin(angleNext);
            float cosNext = MathHelper.cos(angleNext);

            RenderingUtilities.saneVertex(sinThis * circleMin, 0, cosThis * circleMin, 0, 0);
            RenderingUtilities.saneVertex(sinThis * circleMax, 0, cosThis * circleMax, 0, 0);
            RenderingUtilities.saneVertex(sinNext * circleMax, 0, cosNext * circleMax, 0, 0);
            RenderingUtilities.saneVertex(sinNext * circleMin, 0, cosNext * circleMin, 0, 0);
        }
    }

    private void drawCircleSegment(float radius, float startAngle, float endAngle, int segments) {
        float circleMin = radius;
        float circleMax = radius - LINE_THICKNESS;

        for (int i = 0; i < segments; i++) {
            float progressThis = i / (float) segments;
            float angleThis = TheWorkUtils.lerpRadians(progressThis, startAngle, endAngle);
            float sinThis = MathHelper.sin(angleThis);
            float cosThis = MathHelper.cos(angleThis);

            float progressNext = (i + 1) / (float) segments;
            float angleNext = TheWorkUtils.lerpRadians(progressNext, startAngle, endAngle);
            float sinNext = MathHelper.sin(angleNext);
            float cosNext = MathHelper.cos(angleNext);

            RenderingUtilities.saneVertex(sinThis * circleMin, 0, cosThis * circleMin, 0, 0);
            RenderingUtilities.saneVertex(sinThis * circleMax, 0, cosThis * circleMax, 0, 0);
            RenderingUtilities.saneVertex(sinNext * circleMax, 0, cosNext * circleMax, 0, 0);
            RenderingUtilities.saneVertex(sinNext * circleMin, 0, cosNext * circleMin, 0, 0);
        }
    }

    private void drawCirclePips(float radius, float startAngle, float endAngle, int pips, float pipHeight, boolean offsetPips) {
        for (int i = 0; i < pips; i++) {
            float progressThis = (offsetPips ? i + 0.5f : i) / (float) pips;
            float angleThis = TheWorkUtils.lerpRadians(progressThis, startAngle, endAngle);

            drawPip(radius, angleThis, MathHelper.lerp(renderRandom.nextFloat(), -0.03f, 0.03f) + pipHeight);
        }
    }

    private void drawPippedCircleSegment(float radius, float startAngle, float endAngle, int segments, int pips, float pipOffset, float pipHeight) {
        drawCircleSegment(radius, startAngle, endAngle, segments);

        if (Math.abs(pipHeight) > 0.001f)
            drawCirclePips(radius + pipOffset, startAngle, endAngle, pips, pipHeight, true);
    }

    private void drawPip(float radius, float angle, float pipHeight) {
        float tangent = (float) (angle + MathHelper.HALF_PI);
        float tanX = MathHelper.sin(tangent) * HALF_LINE_THICKNESS;
        float tanY = MathHelper.cos(tangent) * HALF_LINE_THICKNESS;

        float closeX = MathHelper.sin(angle) * radius;
        float closeY = MathHelper.cos(angle) * radius;

        float farX = MathHelper.sin(angle) * (radius + pipHeight);
        float farY = MathHelper.cos(angle) * (radius + pipHeight);

        RenderingUtilities.saneVertex(closeX - tanX, 0, closeY - tanY, 0, 0);
        RenderingUtilities.saneVertex(closeX + tanX, 0, closeY + tanY, 0, 0);
        RenderingUtilities.saneVertex(farX + tanX, 0, farY + tanY, 0, 0);
        RenderingUtilities.saneVertex(farX - tanX, 0, farY - tanY, 0, 0);
    }
}
