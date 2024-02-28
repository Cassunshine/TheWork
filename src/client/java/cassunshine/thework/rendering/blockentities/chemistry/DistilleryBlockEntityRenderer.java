package cassunshine.thework.rendering.blockentities.chemistry;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.chemistry.DistilleryBlockEntity;
import cassunshine.thework.blocks.DistilleryBlock;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.rendering.blockentities.AlchemyJarBlockEntityRenderer;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DistilleryBlockEntityRenderer implements BlockEntityRenderer<DistilleryBlockEntity> {

    public DistilleryBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(DistilleryBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        RenderingUtilities.setupStack(matrices);
        RenderingUtilities.setupConsumers(vertexConsumers);
        RenderingUtilities.setupLightOverlay(light, overlay);

        if (!entity.jarStack.isEmpty()) {
            matrices.push();
            matrices.translate(0, 2 / 16.0f, 0);
            RenderingUtilities.renderBlock(TheWorkBlocks.ALCHEMY_JAR_BLOCK.getDefaultState(), light, overlay);

            RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutoutNoCull(new Identifier(TheWorkMod.ModID, "textures/block/alchemy_jar.png")));
            AlchemyJarBlockEntityRenderer.renderNormalLiquid(entity.jarStack.getNbt());
            matrices.pop();
        }

        if (!entity.fuelStack.isEmpty()) {
            matrices.push();
            matrices.translate(6 / 16.0f, 1.5 / 16.0f, 0.5f);

            var bs = entity.getWorld().getBlockState(entity.getPos());
            matrices.multiply(bs.get(DistilleryBlock.FACING).getRotationQuaternion());

            RenderingUtilities.renderItem(entity.fuelStack, entity.getWorld());

            matrices.pop();
        }
    }
}
