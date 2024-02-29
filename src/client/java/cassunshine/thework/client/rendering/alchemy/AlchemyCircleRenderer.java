package cassunshine.thework.client.rendering.alchemy;

import cassunshine.thework.TheWorkClient;
import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.alchemy.circle.path.AlchemyLink;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.client.rendering.blockentities.alchemy_block.nodes.AlchemyNodeTypeRenderers;
import cassunshine.thework.client.rendering.util.RenderingUtilities;
import cassunshine.thework.client.rendering.blockentities.AlchemyJarBlockEntityRenderer;
import cassunshine.thework.utils.TheWorkUtils;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Random;

/**
 * Contains a bunch of helper functions for rendering alchemy circles.
 * <p>
 * Used in both the notebook and the in-world circles.
 * Assumes the matrix stack is already set up correctly for rendering.
 */
public class AlchemyCircleRenderer {

    public static final float LINE_THICKNESS = 1 / 16.0f;
    public static final float HALF_LINE_THICKNESS = LINE_THICKNESS / 2.0f;

    private static final Identifier ALCHEMY_CIRCLE_TEXTURE = new Identifier(TheWorkMod.ModID, "textures/other/alchemy_circle.png");

    public static final Random renderRandom = new Random();
    public static final Random seededRenderRandom = new Random();

    private static final ArrayList<Runnable> deferRenderingTasks = new ArrayList<>();


    public static RenderLayer getLayer() {
        return RenderLayer.getEntityCutoutNoCull(ALCHEMY_CIRCLE_TEXTURE);
    }

    public static RenderLayer getLayer(AlchemyCircle circle) {
        return circle.isActive ? RenderLayer.getEntityTranslucentEmissive(ALCHEMY_CIRCLE_TEXTURE) : RenderLayer.getEntityCutoutNoCull(ALCHEMY_CIRCLE_TEXTURE);
    }

    public static void runDeferTasks() {
        for (Runnable task : deferRenderingTasks)
            task.run();

        deferRenderingTasks.clear();
    }

    /**
     * Draws a full alchemy circle, including all rings and nodes.
     */
    public static void drawAlchemyCircle(AlchemyCircle circle) {
        RenderingUtilities.setupRenderLayer(getLayer(circle));

        RenderingUtilities.translateMatrix(0.5f, 0.01f, 0.5f);

        //Draws the main 'center' of the circle, the one used for the mod logo!
        {
            RenderingUtilities.setupColor(circle.color);

            drawSidedCircle(0.5f, 8);
            drawSidedCircle(0.5f, 4);

            seededRenderRandom.setSeed(circle.blockEntity.getPos().asLong());

            drawPipSegment(0.5f + LINE_THICKNESS, 0, MathHelper.PI, 4, 1 / 8.0f, false);
            drawPipSegment(0.5f + LINE_THICKNESS, MathHelper.PI, MathHelper.TAU, 4, 1 / 8.0f, false);
        }

        for (AlchemyRing ring : circle.rings) {
            drawAlchemyRing(ring);
        }

        runDeferTasks();
    }

    /**
     * Draws the ring of an alchemy circle, which includes all the nodes and links of that ring.
     */
    public static void drawAlchemyRing(AlchemyRing ring) {
        //Set random seed for pops and such.
        seededRenderRandom.setSeed(Float.hashCode(ring.radius));

        for (int i = 0; i < ring.nodes.length; i++) {
            //Calculate the properties for this node.
            var nodeThis = ring.getNode(i);
            var pathThis = ring.paths[i];

            RenderingUtilities.setupColor(ring.color);

            //If node is special, do the special rendering for it later.
            if (nodeThis.sides != 0)
                drawAlchemyNode(nodeThis);
            else
                drawPip(ring.radius - 0.03f - LINE_THICKNESS, nodeThis.getAngle(), 0.06f + LINE_THICKNESS);

            RenderingUtilities.setupColor(nodeThis.color);

            //Draw this node's link, if any.
            if (nodeThis.link != null)
                drawLink(nodeThis.link);

            RenderingUtilities.setupColor(ring.color);

            //Draw the arc between this node and the next.
            int length = MathHelper.ceil(pathThis.length);
            drawPippedCircleSegment(ring.radius, pathThis.startAngle, pathThis.endAngle, length, length * 3, ring.isClockwise ? LINE_THICKNESS : -LINE_THICKNESS * 3, ring.isClockwise ? 0.1f : -0.05f);
        }
    }

    /**
     * Draws an alchemy node.
     */
    public static void drawAlchemyNode(AlchemyNode node) {
        RenderingUtilities.pushMat();

        RenderingUtilities.setupColor(node.color);

        try {
            var customRenderer = AlchemyNodeTypeRenderers.get(node.nodeType);

            RenderingUtilities.translateMatrix(MathHelper.sin(node.getAngle()) * node.ring.radius, 0, MathHelper.cos(node.getAngle()) * node.ring.radius);
            RenderingUtilities.rotateMatrix(0, node.getAngle() + MathHelper.PI, 0);

            //Run custom renderer
            if (customRenderer != null)
                customRenderer.render(node);

            drawSidedCircleAndRune(0.5f, node.sides, node.rune);

            //Render item
            if (!node.heldStack.isEmpty()) {
                RenderingUtilities.pushMat();

                if (node.heldStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == TheWorkBlocks.ALCHEMY_JAR_BLOCK) {

                    deferRenderingTasks.add(() -> {
                        RenderingUtilities.pushMat();

                        RenderingUtilities.translateMatrix(MathHelper.sin(node.getAngle()) * node.ring.radius, 0, MathHelper.cos(node.getAngle()) * node.ring.radius);
                        RenderingUtilities.rotateMatrix(0, node.getAngle() + MathHelper.PI, 0);

                        RenderingUtilities.translateMatrix(-0.5f, 0, -0.5f);
                        RenderingUtilities.renderBlock(TheWorkBlocks.ALCHEMY_JAR_BLOCK.getDefaultState(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);

                        RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutoutNoCull(new Identifier(TheWorkMod.ModID, "textures/block/alchemy_jar.png")));
                        AlchemyJarBlockEntityRenderer.renderNormalLiquid(node.heldStack.getNbt());

                        RenderingUtilities.popMat();
                    });

                } else {
                    deferRenderingTasks.add(() -> {
                        RenderingUtilities.pushMat();

                        RenderingUtilities.translateMatrix(MathHelper.sin(node.getAngle()) * node.ring.radius, 0, MathHelper.cos(node.getAngle()) * node.ring.radius);
                        RenderingUtilities.rotateMatrix(0, node.getAngle() + MathHelper.PI, 0);

                        seededRenderRandom.setSeed(node.getPositionFlat().hashCode());
                        float time = (float) TheWorkClient.getTime();
                        time += seededRenderRandom.nextFloat(MathHelper.TAU);

                        RenderingUtilities.translateMatrix(0, MathHelper.sin((time * 2) % MathHelper.TAU) * 0.1f, 0);
                        RenderingUtilities.rotateMatrix(0, time * 2.0f, 0);

                        RenderingUtilities.renderItem(node.heldStack, node.ring.circle.blockEntity.getWorld());

                        RenderingUtilities.popMat();
                    });
                }

                RenderingUtilities.popMat();
            }

            //TODO - Check perf on this
            var layer = getLayer(node.ring.circle);
            //layer.startDrawing();
            RenderingUtilities.setupRenderLayer(layer);

        } catch (Exception e) {
            TheWorkMod.LOGGER.error(e.toString());
        }

        RenderingUtilities.popMat();
    }

    public static void drawLink(AlchemyLink link) {
        var sourcePosition = link.sourceNode.getPositionRelative();
        var delta = link.destinationNode.getPositionRelative().subtract(sourcePosition);

        float angle = (float) MathHelper.atan2(-delta.z, delta.x) + MathHelper.PI * 0.5f;
        float length = link.length;

        RenderingUtilities.pushMat();
        RenderingUtilities.translateMatrix(sourcePosition.x, 0, sourcePosition.z);
        RenderingUtilities.rotateMatrix(0, angle, 0);

        if (link.sourceNode.nodeType != AlchemyNodeTypes.NONE) {
            RenderingUtilities.translateMatrix(0, 0, 0.5f);
        } else {
            RenderingUtilities.translateMatrix(0, 0, LINE_THICKNESS * 2);
            length -= LINE_THICKNESS * 2;
        }

        length -= LINE_THICKNESS;

        RenderingUtilities.saneVertex(-HALF_LINE_THICKNESS, 0, 0, 0, 0);
        RenderingUtilities.saneVertex(HALF_LINE_THICKNESS, 0, 0, 0, 0);
        RenderingUtilities.saneVertex(HALF_LINE_THICKNESS, 0, length, 0, 0);
        RenderingUtilities.saneVertex(-HALF_LINE_THICKNESS, 0, length, 0, 0);

        RenderingUtilities.popMat();
    }

    /**
     * Draws a circle with the specified number of sides and a rune.
     */
    public static void drawSidedCircleAndRune(float radius, int sides, Identifier rune) {
        drawSidedCircleAndRune(radius, sides, rune, 0);
    }

    /**
     * Draws a circle with the specified number of sides and a rune.
     */
    public static void drawSidedCircleAndRune(float radius, int sides, Identifier rune, float runeAngle) {
        RenderingUtilities.pushMat();

        if (sides == 4) {
            drawSidedCircle(radius, sides);
        } else {
            drawSidedCircle(radius, sides);
        }

        RenderingUtilities.rotateMatrix(0, runeAngle, 0);

        if (!rune.equals(TheWorkRunes.NULL))
            drawRune(rune);

        RenderingUtilities.popMat();
    }

    public static void drawRune(Identifier rune) {
        //deferRenderingTasks.add(() -> {
        var sprite = rune;
        if (sprite == null || sprite.equals(TheWorkRunes.NULL))
            return;

        //Modify rune to point to rune texture.
        sprite = sprite.withPath("textures/runes/" + sprite.getPath() + ".png");

        //Move to center of rune.
        RenderingUtilities.translateMatrix(-0.5f, 0, -0.5f);

        RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutoutNoCull(sprite));

        RenderingUtilities.saneVertex(0, 0, 0, 0, 0);
        RenderingUtilities.saneVertex(0, 0, 1, 0, 1);
        RenderingUtilities.saneVertex(1, 0, 1, 1, 1);
        RenderingUtilities.saneVertex(1, 0, 0, 1, 0);
        //});
    }

    public static void drawPippedCircleSegment(float radius, float startAngle, float endAngle, int segments, int pips, float pipOffset, float pipHeight) {
        drawCircleSegment(radius, startAngle, endAngle, segments);

        if (Math.abs(pipHeight) > 0.001f)
            drawPipSegment(radius + pipOffset, startAngle, endAngle, pips, pipHeight, true);
    }

    /**
     * Draws a circle with the specified amount of sides.
     */
    public static void drawSidedCircle(float radius, int sides) {
        float circleMax = radius - LINE_THICKNESS;
        if (sides == 3)
            circleMax -= LINE_THICKNESS * 0.5f;

        for (int i = 0; i < sides; i++) {
            float progressThis = i / (float) sides;
            float angleThis = progressThis * MathHelper.TAU;
            float sinThis = MathHelper.sin(angleThis);
            float cosThis = MathHelper.cos(angleThis);

            float progressNext = (i + 1) / (float) sides;
            float angleNext = progressNext * MathHelper.TAU;
            float sinNext = MathHelper.sin(angleNext);
            float cosNext = MathHelper.cos(angleNext);

            RenderingUtilities.saneVertex(sinThis * radius, 0, cosThis * radius, 0, 0);
            RenderingUtilities.saneVertex(sinThis * circleMax, 0, cosThis * circleMax, 0, 0);
            RenderingUtilities.saneVertex(sinNext * circleMax, 0, cosNext * circleMax, 0, 0);
            RenderingUtilities.saneVertex(sinNext * radius, 0, cosNext * radius, 0, 0);
        }
    }

    /**
     * Draws the segment of a circle with the given number of sides.
     */
    public static void drawCircleSegment(float radius, float startAngle, float endAngle, int segments) {
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

            RenderingUtilities.saneVertex(sinThis * radius, 0, cosThis * radius, 0, 0);
            RenderingUtilities.saneVertex(sinThis * circleMax, 0, cosThis * circleMax, 0, 0);
            RenderingUtilities.saneVertex(sinNext * circleMax, 0, cosNext * circleMax, 0, 0);
            RenderingUtilities.saneVertex(sinNext * radius, 0, cosNext * radius, 0, 0);
        }
    }


    /**
     * Draws pips along a segment of a circle.
     */
    public static void drawPipSegment(float radius, float startAngle, float endAngle, int pips, float pipHeight, boolean offsetPips) {
        for (int i = 0; i < pips; i++) {
            float progressThis = (offsetPips ? i + 0.5f : i) / (float) pips;
            float angleThis = TheWorkUtils.lerpRadians(progressThis, startAngle, endAngle);

            drawPip(radius, angleThis, MathHelper.lerp(seededRenderRandom.nextFloat(), -0.03f, 0.03f) + pipHeight);
        }
    }


    /**
     * Draws a single pip.
     */
    public static void drawPip(float radius, float angle, float pipHeight) {
        float tangent = angle + MathHelper.HALF_PI;
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
