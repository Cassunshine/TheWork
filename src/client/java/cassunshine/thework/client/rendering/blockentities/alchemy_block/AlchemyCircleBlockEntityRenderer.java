package cassunshine.thework.client.rendering.blockentities.alchemy_block;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.client.rendering.util.RenderingUtilities;
import cassunshine.thework.client.rendering.alchemy.AlchemyCircleRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Random;

public class AlchemyCircleBlockEntityRenderer implements BlockEntityRenderer<AlchemyCircleBlockEntity> {

    public static final Random renderRandom = new Random();
    public static final Random seededRenderRandom = new Random();

    private static final float LINE_THICKNESS = 1 / 16.0f;
    private static final float HALF_LINE_THICKNESS = LINE_THICKNESS / 2.0f;

    private static final Identifier ALCHEMY_CIRCLE_TEXTURE = new Identifier(TheWorkMod.ModID, "textures/other/alchemy_circle.png");

    private ArrayList<AlchemyNode> specialRenders = new ArrayList<>();

    public boolean wobble = false;

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

        if (!circle.isActive) {
            RenderingUtilities.setupColor(240, 240, 240, 255);
        } else {
            RenderingUtilities.setupColor(230, 240, 255, 255);
        }

        RenderingUtilities.setupWobble(circle.isActive ? 0.01f : 0);

        try {
            AlchemyCircleRenderer.drawAlchemyCircle(circle);
        } catch (Exception e) {
            TheWorkMod.LOGGER.error(e.toString());
        }

        RenderingUtilities.setupWobble(0);
        RenderingUtilities.popMat();
    }
}
