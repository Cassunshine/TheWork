package cassunshine.thework.rendering.blockentities.alchemy_block;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeType;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeTypes;
import cassunshine.thework.rendering.blockentities.alchemy_block.nodes.NodeTypeRenderers;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AlchemyCircleBlockEntityRenderer implements BlockEntityRenderer<AlchemyCircleBlockEntity> {

    private static final Identifier ALCHEMY_CIRCLE_TEXTURE = new Identifier(TheWorkMod.ModID, "textures/other/alchemy_circle.png");

    public AlchemyCircleBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(AlchemyCircleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        RenderingUtilities.setupStack(matrices);
        RenderingUtilities.setupLightOverlay(light, overlay);

        RenderingUtilities.pushMat();
        RenderingUtilities.setupConsumers(vertexConsumers);
        RenderingUtilities.setupRenderLayer(getLayer());

        try {
            matrices.translate(0.5f, 1 / 32.0f, 0.5f);

            for (int i = 0; i < entity.mainCircles.size(); i++) {
                AlchemyCircleBlockEntity.MainCircle circle = entity.mainCircles.get(i);
                AlchemyCircleBlockEntity.MainCircle nextCircle = i + 1 >= entity.mainCircles.size() ? null : entity.mainCircles.get(i + 1);

                drawMainCircle(circle, nextCircle == null ? -1 : nextCircle.radius);
            }

        } catch (Exception e) {
            //Do nothing.
        } finally {
            RenderingUtilities.popMat();
        }
    }

    @Override
    public int getRenderDistance() {
        return 999;
    }

    public RenderLayer getLayer() {
        return RenderLayer.getEntityCutoutNoCull(ALCHEMY_CIRCLE_TEXTURE);
    }

    @Override
    public boolean rendersOutsideBoundingBox(AlchemyCircleBlockEntity blockEntity) {
        return true;
    }

    private void drawMainCircle(AlchemyCircleBlockEntity.MainCircle circle, float nextRadius) {
        float anglePerNode = MathHelper.TAU / circle.nodes.length;
        float lastAngle = 0;

        float smallCircleCover = ((0.5f / circle.circumference) * MathHelper.TAU);

        for (int i = 0; i < circle.nodes.length; i++) {
            NodeType node = circle.nodes[i];
            NodeType nextNode = circle.nodes[(i + 1) % circle.nodes.length];

            float thisAngle = node == NodeTypes.NONE ? lastAngle : lastAngle + smallCircleCover;
            float nextAngle = nextNode == NodeTypes.NONE ? lastAngle + anglePerNode : lastAngle + anglePerNode - smallCircleCover;

            float pipHeight = 0.1f;
            float pipOffset = 0;

            //Draw sub-circle
            if (node != NodeTypes.NONE) {
                RenderingUtilities.pushMat();
                float drawX = MathHelper.sin(thisAngle - smallCircleCover) * circle.radius;
                float drawZ = MathHelper.cos(thisAngle - smallCircleCover) * circle.radius;

                RenderingUtilities.translateMatrix(drawX, 0, drawZ);

                drawCircleSegment(0.5f, 0, MathHelper.TAU);

                RenderingUtilities.rotateMatrix(0, -lastAngle, 0);
                RenderingUtilities.translateMatrix(-0.5f, 0, -0.5f);

                //Special node renderer, if any.
                var renderer = NodeTypeRenderers.get(node.getClass());
                if (renderer != null) {
                    renderer.render(node);
                    RenderingUtilities.setupRenderLayer(getLayer());
                }


                RenderingUtilities.popMat();

                if (nextRadius > 0) {
                    pipOffset = 0.5f;
                    pipHeight = (nextRadius - circle.radius) - pipOffset;
                } else {
                    pipOffset = 0.5f;
                }
            }


            if (pipHeight > 0.001f)
                drawPip(circle.radius + pipOffset, lastAngle, 1 / 32.0f, pipHeight);

            //Draw line to next sub-circle.
            drawCircleSegment(circle.radius, thisAngle, nextAngle);
            lastAngle += anglePerNode;
        }

        if (circle.nodes.length == 0)
            drawCircleSegment(circle.radius, 0, MathHelper.TAU);
    }

    private void drawCircleSegment(float radius, float startAngle, float endAngle) {

        float circleHalfThickness = 1 / 32.0f;
        float circleMin = radius - circleHalfThickness;
        float circleMax = radius + circleHalfThickness;

        //Total circumference of the circle.
        float totalCircumference = 2 * MathHelper.PI * radius;
        //The length of the segment we're drawing.
        float deltaCircumference = totalCircumference * (MathHelper.abs(startAngle - endAngle) / MathHelper.TAU);

        //Draw 1 segment per block length, rounding up, times 2 so it's always even(?)
        int drawSegments = MathHelper.ceil(deltaCircumference);

        if (radius < 1)
            drawSegments *= 4;

        float startCircumference = totalCircumference * (startAngle / MathHelper.TAU);

        for (int i = 0; i < drawSegments; i++) {
            float angleThis = MathHelper.lerp(i / (float) drawSegments, startAngle, endAngle);
            float angleNext = MathHelper.lerp((i + 1) / (float) drawSegments, startAngle, endAngle);

            float startSin = MathHelper.sin(angleThis);
            float startCos = MathHelper.cos(angleThis);

            float nextSin = MathHelper.sin(angleNext);
            float nextCos = MathHelper.cos(angleNext);

            RenderingUtilities.saneVertex(startSin * circleMin, 0, startCos * circleMin, 255, 255, 255, startCircumference, 0, 0, 1, 0);
            RenderingUtilities.saneVertex(startSin * circleMax, 0, startCos * circleMax, 255, 255, 255, startCircumference, 0.125f, 0, 1, 0);

            startCircumference += 1;

            RenderingUtilities.saneVertex(nextSin * circleMax, 0, nextCos * circleMax, 255, 255, 255, startCircumference, 0.125f, 0, 1, 0);
            RenderingUtilities.saneVertex(nextSin * circleMin, 0, nextCos * circleMin, 255, 255, 255, startCircumference, 0, 0, 1, 0);
        }
    }

    private void drawPip(float radius, float angle, float pipWidth, float pipHeight) {
        float circumference = 2 * MathHelper.PI * radius;
        float angleWidth = (pipWidth / circumference) * MathHelper.TAU;

        float topCircumference = 2 * MathHelper.PI * (radius + pipHeight);
        float topAngleWidth = (pipWidth / topCircumference) * MathHelper.TAU;

        float minRadius = radius;
        float maxRadius = radius + pipHeight;

        RenderingUtilities.saneVertex(MathHelper.sin(angle - angleWidth) * minRadius, 0, MathHelper.cos(angle - angleWidth) * minRadius, 255, 255, 255, 0, 0, 0, 1, 0);
        RenderingUtilities.saneVertex(MathHelper.sin(angle - topAngleWidth) * maxRadius, 0, MathHelper.cos(angle - topAngleWidth) * maxRadius, 255, 255, 255, 0, 0, 0, 1, 0);
        RenderingUtilities.saneVertex(MathHelper.sin(angle + topAngleWidth) * maxRadius, 0, MathHelper.cos(angle + topAngleWidth) * maxRadius, 255, 255, 255, 0, 0, 0, 1, 0);
        RenderingUtilities.saneVertex(MathHelper.sin(angle + angleWidth) * minRadius, 0, MathHelper.cos(angle + angleWidth) * minRadius, 255, 255, 255, 0, 0, 0, 1, 0);
    }
}
