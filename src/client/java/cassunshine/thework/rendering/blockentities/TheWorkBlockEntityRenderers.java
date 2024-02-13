package cassunshine.thework.rendering.blockentities;

import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.rendering.blockentities.alchemy_block.AlchemyCircleBlockEntityRenderer;
import cassunshine.thework.rendering.blockentities.chemistry.ChemistrySetBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class TheWorkBlockEntityRenderers {

    public static void initialize() {
        BlockEntityRendererFactories.register(TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE, AlchemyCircleBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(TheWorkBlockEntities.CHEMISTRY_SET_TYPE, ChemistrySetBlockEntityRenderer::new);
    }
}
