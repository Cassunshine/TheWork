package cassunshine.thework;

import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.client.networking.TheWorkClientNetworking;
import cassunshine.thework.rendering.blockentities.TheWorkBlockEntityRenderers;
import cassunshine.thework.rendering.blockentities.alchemy_block.nodes.AlchemyNodeTypeRenderers;
import cassunshine.thework.rendering.entities.TheWorkEntityRenderers;
import cassunshine.thework.rendering.model.TheWorkModelPlugin;
import cassunshine.thework.rendering.particles.TheWorkParticleRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheWorkClient implements ClientModInitializer {
    private static final double RENDER_TIME_MULT = 1.0d / 1_000_000_000d;

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


        ModelLoadingPlugin.register(new TheWorkModelPlugin());
    }

    public static double getTime() {
        var world = MinecraftClient.getInstance().world;

        if (world == null)
            return 0;
        return (world.getTime() + MinecraftClient.getInstance().getTickDelta()) / 20.0f;
    }


}