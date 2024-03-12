package cassunshine.thework;

import cassunshine.thework.assets.JournalLayouts;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.client.networking.TheWorkClientNetworking;
import cassunshine.thework.client.rendering.blockentities.TheWorkBlockEntityRenderers;
import cassunshine.thework.client.rendering.blockentities.alchemy_block.nodes.AlchemyNodeTypeRenderers;
import cassunshine.thework.client.rendering.entities.TheWorkEntityRenderers;
import cassunshine.thework.client.rendering.items.AlchemistNotebookRenderer;
import cassunshine.thework.client.rendering.model.TheWorkModelPlugin;
import cassunshine.thework.client.rendering.particles.TheWorkParticleRenderers;
import cassunshine.thework.items.TheWorkItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.mixin.client.rendering.TooltipComponentMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
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
        BlockRenderLayerMap.INSTANCE.putBlock(TheWorkBlocks.DISTILLERY_BLOCK, RenderLayer.getCutout());


        ModelLoadingPlugin.register(new TheWorkModelPlugin());
        //BuiltinItemRendererRegistry.INSTANCE.register(BlockItem.BLOCK_ITEMS.get(TheWorkBlocks.ALCHEMY_JAR_BLOCK), AlchemyJarBlockEntityRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM, AlchemistNotebookRenderer::renderItem);


        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(TheWorkMod.ModID, "client_resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                JournalLayouts.loadLayouts(manager);
            }
        });
    }

    public static double getTime() {
        var world = MinecraftClient.getInstance().world;

        if (world == null)
            return 0;

        if (MinecraftClient.getInstance().isPaused())
            return world.getTime();
        return (world.getTime() + MinecraftClient.getInstance().getTickDelta()) / 20.0f;
    }


}