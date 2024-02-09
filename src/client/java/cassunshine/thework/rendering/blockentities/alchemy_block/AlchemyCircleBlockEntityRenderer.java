package cassunshine.thework.rendering.blockentities.alchemy_block;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.alchemycircle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemycircle.nodes.types.AlchemyNodeTypes;
import cassunshine.thework.blockentities.alchemycircle.rings.AlchemyRing;
import cassunshine.thework.particles.TheWorkParticles;
import cassunshine.thework.rendering.blockentities.alchemy_block.nodes.AlchemyNodeTypeRenderers;
import cassunshine.thework.rendering.particles.RadialParticle;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

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
        RenderingUtilities.setupRenderLayer(getLayer(entity));

        float time = entity.getWorld().getTime() + MinecraftClient.getInstance().getTickDelta();

        try {
            RenderingUtilities.translateMatrix(0.5f, 1 / 32.0f, 0.5f);

            /*if (entity.isActive) {
                entity.animationRotation += MinecraftClient.getInstance().getLastFrameDuration() * 0.1f;
                entity.animationRotation %= MathHelper.TAU;
            } else {
                entity.animationRotation = MathHelper.lerpAngleDegrees(0.1f, (entity.animationRotation / MathHelper.TAU) * 360, 0);
            }

            RenderingUtilities.rotateMatrix(0, entity.animationRotation, 0);*/


            ArrayList<AlchemyRing> rings = entity.rings;

            for (int i = 0; i < rings.size(); i++) {
                AlchemyRing nextRing = entity.isOutward ? (i + 1 >= rings.size() ? null : rings.get(i + 1)) : (i - 1 < 0 ? null : rings.get(i - 1));

                drawRing(rings.get(i), nextRing, entity.isOutward);
            }


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

    public RenderLayer getLayer(AlchemyCircleBlockEntity entity) {
        return entity.isActive ? RenderLayer.getEntityTranslucentEmissive(ALCHEMY_CIRCLE_TEXTURE) : RenderLayer.getEntityCutoutNoCull(ALCHEMY_CIRCLE_TEXTURE);
    }

    @Override
    public boolean rendersOutsideBoundingBox(AlchemyCircleBlockEntity blockEntity) {
        return true;
    }

    private void drawRing(AlchemyRing ring, AlchemyRing nextRing, boolean outward) {

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

        var ringPos = ring.circle.fullPosition.add(0, 0.1f, 0);
        var random = ring.circle.getWorld().random;

        for (int i = 0; i < ring.nodes.length; i++) {
            //Current and nextRing node in the ring.
            var currentNode = ring.getNode(i);
            var nextNode = ring.getNode(i + 1);

            //Angles of the line segment being drawn out of this node.
            float segmentStartAngle = currentNode.type == AlchemyNodeTypes.NONE ? lastAngle : lastAngle + nodeSizeAngle;
            float segmentEndAngle = nextNode.type == AlchemyNodeTypes.NONE ? lastAngle + anglePerNode : lastAngle + anglePerNode - nodeSizeAngle;

            float nextRadius = nextRing == null ? ring.radius + 0.1f : nextRing.radius;
            nextRadius = nextRing == null ? nextRadius : nextRadius + (nextRadius - (nextRing.getClosestRadius(lastAngle))) * (outward ? -1 : 1);

            float pipOffset = 0;
            float pipHeight = (nextRadius - ring.radius);

            //If the current node is special
            if (currentNode.type != AlchemyNodeTypes.NONE) {
                //Draw circle.
                RenderingUtilities.pushMat();

                //Move rendering to where the alchemy node is in the world.
                var pos = currentNode.position.subtract(ring.circle.fullPosition);
                RenderingUtilities.translateMatrix((float) pos.x, 0, (float) pos.z);

                var customDraw = AlchemyNodeTypeRenderers.get(currentNode.type);
                int segments = customDraw == null ? 8 : customDraw.circleSides;

                RenderingUtilities.rotateMatrix(0, lastAngle, 0);
                //Draw a circle where the node is.
                drawCircleSegment(0.5f, 0, MathHelper.TAU, segments);
                RenderingUtilities.popMat();

                //Calculate offset for pip
                pipOffset += outward ? 0.5f : -0.5f;
                if (nextRing == null)
                    pipHeight = 0;
                else
                    pipHeight += outward ? -0.5f : 0.5f;

                //Queue up special renderer for later.
                nodesToDraw.add(currentNode);

                //Draw particles for each element in this node's inventory, if the circle is active.
                if (ring.circle.isActive && !MinecraftClient.getInstance().isPaused()) {
                    for (var entry : currentNode.inventory.amounts.object2FloatEntrySet()) {
                        if (random.nextInt(10) < 8)
                            continue;

                        var particleStart = MathHelper.lerp(random.nextFloat(), 0, MathHelper.TAU);

                        RadialParticle.color = entry.getKey().color;
                        //Draw particles
                        ring.circle.getWorld().addParticle(
                                TheWorkParticles.RADIAL_ELEMENT,
                                currentNode.position.getX() + MathHelper.lerp(random.nextFloat(), -0.05f, 0.05f),
                                currentNode.position.getY() + MathHelper.lerp(random.nextFloat(), -0.05f, 0.05f) + 0.1f,
                                currentNode.position.getZ() + MathHelper.lerp(random.nextFloat(), -0.05f, 0.05f),
                                0.5f, particleStart, particleStart + MathHelper.TAU
                        );
                    }
                }

            } else {
                pipHeight = outward ? 0.1f : -0.1f;
            }

            if (Math.abs(pipHeight) > 0.0001)
                drawPip(ring.radius + pipOffset, lastAngle, 1 / 32.0f, pipHeight);

            //Draw segment.
            drawCircleSegment(ring.radius, segmentStartAngle, segmentEndAngle);

            float innerSign = ring.isClockwise ? 1 / 16.0f : -(1 / 16.0f);
            drawSegmentPips(ring.radius, segmentStartAngle, segmentEndAngle, innerSign, innerSign);


            if (!ring.isClockwise) {
                segmentStartAngle -= nodeSizeAngle * 2;
                segmentEndAngle = segmentStartAngle + ((segmentEndAngle - segmentStartAngle) * (ring.isClockwise ? 1 : -1));
                segmentEndAngle += nodeSizeAngle * 2;
            }

            //Spawn particles for path to next node.
            if (ring.circle.isActive && !MinecraftClient.getInstance().isPaused()) {
                for (var entry : currentNode.nextNodeOutput.amounts.object2FloatEntrySet()) {
                    if (random.nextInt(10) < 3)
                        continue;

                    RadialParticle.color = entry.getKey().color;

                    //Draw particles
                    ring.circle.getWorld().addParticle(
                            TheWorkParticles.RADIAL_ELEMENT,
                            ringPos.getX() + MathHelper.lerp(random.nextFloat(), -0.05f, 0.05f),
                            ringPos.getY() + MathHelper.lerp(random.nextFloat(), -0.05f, 0.05f),
                            ringPos.getZ() + MathHelper.lerp(random.nextFloat(), -0.05f, 0.05f),
                            ring.radius, segmentStartAngle, segmentEndAngle
                    );
                }
            }

            //Increase angle by 1 node.
            lastAngle += anglePerNode;
        }
    }

    private void drawNode(AlchemyNode node) {
        RenderingUtilities.pushMat();

        //Move and rotate drawing to match node.
        var pos = node.position.subtract(node.ring.circle.fullPosition);
        RenderingUtilities.translateMatrix((float) pos.x, 0, (float) pos.z);
        RenderingUtilities.rotateMatrix(0, node.rotation - MathHelper.PI, 0);

        var customDraw = AlchemyNodeTypeRenderers.get(node.type);
        if (customDraw != null)
            customDraw.render(node);

        if (!node.item.isEmpty()) {
            var world = node.ring.circle.getWorld();
            var blockPos = BlockPos.ofFloored(node.position);
            var light = LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, blockPos), world.getLightLevel(LightType.SKY, blockPos));

            float random = (node.position.hashCode() % 1000) / 100.0f;
            float time = world.getTime() + MinecraftClient.getInstance().getTickDelta();
            RenderingUtilities.rotateMatrix(0, (time + random) / 10.0f, 0);
            RenderingUtilities.translateMatrix(0, (MathHelper.sin(((time * 0.05f) + random) % MathHelper.TAU) + 1) * 0.1f, 0);

            RenderingUtilities.renderItem(node.item, node.ring.circle.getWorld(), light, OverlayTexture.DEFAULT_UV);
        }

        RenderingUtilities.popMat();
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

    private void drawCircleSegment(float radius, float startAngle, float endAngle, int drawSegments) {

        float circleHalfThickness = 1 / 32.0f;
        float circleMin = radius - circleHalfThickness;
        float circleMax = radius + circleHalfThickness;

        //Total circumference of the circle.
        float totalCircumference = 2 * MathHelper.PI * radius;

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
