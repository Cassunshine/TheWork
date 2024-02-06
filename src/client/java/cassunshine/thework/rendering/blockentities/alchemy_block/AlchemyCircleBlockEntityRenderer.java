package cassunshine.thework.rendering.blockentities.alchemy_block;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeTypes;
import cassunshine.thework.blockentities.alchemy_circle.rings.AlchemyRing;
import cassunshine.thework.rendering.blockentities.alchemy_block.nodes.NodeTypeRenderers;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;

public class AlchemyCircleBlockEntityRenderer implements BlockEntityRenderer<AlchemyCircleBlockEntity> {

    private static final Identifier ALCHEMY_CIRCLE_TEXTURE = new Identifier(TheWorkMod.ModID, "textures/other/alchemy_circle.png");

    private ArrayList<AlchemyNode> nodesToDraw = new ArrayList<>();

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
            RenderingUtilities.translateMatrix(0.5f, 1 / 32.0f, 0.5f);

            ArrayList<AlchemyRing> rings = entity.rings;
            for (int i = 0; i < rings.size(); i++)
                drawRing(rings.get(i), i + 1 >= rings.size() ? null : rings.get(i + 1));

            nodesToDraw.sort(Comparator.comparing(a -> a.type.id));
            for (AlchemyNode node : nodesToDraw) {
                drawNode(node);
            }
            nodesToDraw.clear();

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

    private void drawRing(AlchemyRing ring, AlchemyRing nextRing) {

        //Small circles just draw as-is.
        if (ring.nodes.length == 0) {
            drawCircleSegment(ring.radius, 0, MathHelper.TAU);
            return;
        }

        //How much angle between each node.
        float anglePerNode = MathHelper.TAU / ring.nodes.length;
        //The angle where the last node was drawn.
        float lastAngle = 0;

        //The size of the node's sub-circle, in angle.
        float nodeSizeAngle = (0.5f / ring.circumference) * MathHelper.TAU;

        for (int i = 0; i < ring.nodes.length; i++) {
            //Current and nextRing node in the ring.
            var currentNode = ring.getNode(i);
            var nextNode = ring.getNode(i + 1);

            //Angles of the line segment being drawn out of this node.
            float segmentStartAngle = currentNode.type == NodeTypes.NONE ? lastAngle : lastAngle + nodeSizeAngle;
            float segmentEndAngle = nextNode.type == NodeTypes.NONE ? lastAngle + anglePerNode : lastAngle + anglePerNode - nodeSizeAngle;

            float pipOffset = 0;
            float nextRadius = nextRing == null ? ring.radius + 0.1f : nextRing.getClosestRadius(lastAngle);
            float pipHeight = (nextRadius - ring.radius);

            //If the current node is special
            if (currentNode.type != NodeTypes.NONE) {
                //Draw circle.
                RenderingUtilities.pushMat();

                var pos = currentNode.position.subtract(currentNode.ring.position);
                RenderingUtilities.translateMatrix((float)pos.x, 0, (float)pos.z);

                drawCircleSegment(0.5f, 0, MathHelper.TAU);
                RenderingUtilities.popMat();

                //Calculate offset for pip
                pipOffset += 0.5f;
                pipHeight -= 0.5f;

                //Queue up special renderer for later.
                nodesToDraw.add(currentNode);

                if(nextRing == null)
                    pipHeight = 0;
            } else {
                pipHeight = 0.1f;
            }

            if(pipHeight > 0.0001)
                drawPip(ring.radius + pipOffset, lastAngle, 1 / 32.0f, pipHeight);

            //Draw segment.
            drawCircleSegment(ring.radius, segmentStartAngle, segmentEndAngle);

            float innerSign = ring.isClockwise ? 1/16.0f : -(1/16.0f);
            drawSegmentPips(ring.radius, segmentStartAngle, segmentEndAngle, innerSign, innerSign);

            //Increase angle by 1 node.
            lastAngle += anglePerNode;
        }
    }

    private void drawNode(AlchemyNode node) {
        var customDraw = NodeTypeRenderers.get(node.type.getClass());

        if(customDraw != null){
            RenderingUtilities.pushMat();
            var pos = node.position.subtract(node.ring.position);
            RenderingUtilities.translateMatrix((float)pos.x, 0, (float)pos.z);
            RenderingUtilities.rotateMatrix(0, -node.rotation, 0);

            customDraw.render(node);

            RenderingUtilities.popMat();
        }
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

    private void drawSegmentPips(float radius, float startAngle, float endAngle, float size, float offset) {

        //Total circumference of the circle.
        float totalCircumference = 2 * MathHelper.PI * radius;
        //The length of the segment we're drawing.
        float deltaCircumference = totalCircumference * (MathHelper.abs(startAngle - endAngle) / MathHelper.TAU);

        //Draw 3 pip per block length
        int drawSegments = MathHelper.ceil(deltaCircumference * 3);

        if (radius < 1)
            drawSegments *= 4;

        for (int i = 1; i < drawSegments; i++) {
            float angleThis = MathHelper.lerp(i / (float) drawSegments, startAngle, endAngle);

            drawPip(radius + offset, angleThis, 1 / 32.0f, size);
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
