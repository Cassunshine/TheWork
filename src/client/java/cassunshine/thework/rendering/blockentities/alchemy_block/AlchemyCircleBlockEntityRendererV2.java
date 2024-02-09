package cassunshine.thework.rendering.blockentities.alchemy_block;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.alchemycircle.rings.AlchemyRing;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AlchemyCircleBlockEntityRendererV2 implements BlockEntityRenderer<AlchemyCircleBlockEntity> {

    private static final float LINE_THICKNESS = 1 / 16.0f;
    private static final float HALF_LINE_THICKNESS = LINE_THICKNESS / 2.0f;

    private static final Identifier ALCHEMY_CIRCLE_TEXTURE = new Identifier(TheWorkMod.ModID, "textures/other/alchemy_circle.png");

    @Override
    public int getRenderDistance() {
        return 1024;
    }

    @Override
    public boolean rendersOutsideBoundingBox(AlchemyCircleBlockEntity blockEntity) {
        return true;
    }

    public RenderLayer getLayer(AlchemyCircleBlockEntity entity) {
        return entity.isActive ? RenderLayer.getEntityTranslucentEmissive(ALCHEMY_CIRCLE_TEXTURE) : RenderLayer.getEntityCutoutNoCull(ALCHEMY_CIRCLE_TEXTURE);
    }

    @Override
    public void render(AlchemyCircleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        RenderingUtilities.setupStack(matrices);

        RenderingUtilities.pushMat();
        RenderingUtilities.setupConsumers(vertexConsumers);
        RenderingUtilities.setupRenderLayer(getLayer(entity));

        RenderingUtilities.setupLightOverlay(light, overlay);
        RenderingUtilities.setupNormal(0, 1, 0);
        RenderingUtilities.setupColor(255, 255, 255, 255);

        try {
            //Offset to middle of block, and move up a little to prevent z-fighting.
            RenderingUtilities.translateMatrix(0.5f, 1 / 32.0f, 0.5f);

            for (int i = 0; i < entity.rings.size(); i++) {
                var ring = entity.rings.get(i);
                var nextRing = entity.isOutward ?
                        (i == entity.rings.size() - 1 ? null : entity.rings.get(i + 1)) :
                        (i == 0 ? null : entity.rings.get(i - 1));


            }

        } catch (Exception e) {
            TheWorkMod.LOGGER.error(e.toString());
        }

        RenderingUtilities.popMat();
    }


    private void drawRing(AlchemyRing ring, AlchemyRing next) {

        for (int i = 0; i < ring.nodes.length; i++) {
            //var nodeThis = ring.getNode(i);
            //var nextNode = ring.getNode(ring.isClockwise ? ring.);
        }

    }


    private void drawCircleSegment(float radius, float startAngle, float endAngle, int segments) {
        float circleMin = radius - HALF_LINE_THICKNESS;
        float circleMax = radius + HALF_LINE_THICKNESS;

        for (int i = 0; i < segments; i++) {
            float progressThis = i / (float) segments;
            float progressNext = (i + 1) / (float) segments;

            float angleThis = MathHelper.lerp(progressThis, startAngle, endAngle);
            float angleNext = MathHelper.lerp(progressNext, startAngle, endAngle);

            float sinThis = MathHelper.sin(angleThis);
            float cosThis = MathHelper.sin(angleThis);

            float sinNext = MathHelper.sin(angleNext);
            float cosNext = MathHelper.sin(angleNext);

            RenderingUtilities.saneVertex(sinThis * circleMin, 0, cosThis * circleMin, 0, 0);
            RenderingUtilities.saneVertex(sinThis * circleMax, 0, cosThis * circleMax, 0, 0);
            RenderingUtilities.saneVertex(sinNext * circleMin, 0, cosNext * circleMin, 0, 0);
            RenderingUtilities.saneVertex(sinNext * circleMax, 0, cosNext * circleMax, 0, 0);
        }
    }
}
