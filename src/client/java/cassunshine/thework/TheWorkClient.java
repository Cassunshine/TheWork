package cassunshine.thework;

import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.client.networking.TheWorkClientNetworking;
import cassunshine.thework.particles.TheWorkParticles;
import cassunshine.thework.rendering.blockentities.TheWorkBlockEntityRenderers;
import cassunshine.thework.rendering.blockentities.alchemy_block.nodes.AlchemyNodeTypeRenderers;
import cassunshine.thework.rendering.entities.TheWorkEntityRenderers;
import cassunshine.thework.rendering.particles.TheWorkParticleRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheWorkClient implements ClientModInitializer {
    public static final Logger CLIENT_LOGGER = LoggerFactory.getLogger(TheWorkClient.class);

    @Override
    public void onInitializeClient() {
        TheWorkBlockEntityRenderers.initialize();
        TheWorkParticleRenderers.initialize();
        TheWorkEntityRenderers.initialize();
        TheWorkClientNetworking.initialize();

        AlchemyNodeTypeRenderers.initialize();

        TheWorkClientNetworking.initialize();
        BlockRenderLayerMap.INSTANCE.putBlock(TheWorkBlocks.ALCHEMY_JAR_BLOCK, RenderLayer.getTranslucent());
    }
}