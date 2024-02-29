package cassunshine.thework.client.rendering.blockentities;

import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.client.rendering.blockentities.alchemy_block.AlchemyCircleBlockEntityRenderer;
import cassunshine.thework.client.rendering.blockentities.chemistry.DistilleryBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class TheWorkBlockEntityRenderers {

    public static void initialize() {
        BlockEntityRendererFactories.register(TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE, AlchemyCircleBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(TheWorkBlockEntities.DISTILLERY_TYPE, DistilleryBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(TheWorkBlockEntities.ALCHEMY_JAR_TYPE, AlchemyJarBlockEntityRenderer::new);
    }
}
