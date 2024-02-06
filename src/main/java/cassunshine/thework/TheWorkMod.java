package cassunshine.thework;

import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeTypes;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.elements.TheWorkElements;
import cassunshine.thework.elements.recipes.TheWorkRecipes;
import cassunshine.thework.entities.TheWorkEntities;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.network.TheWorkNetworking;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.event.interaction.InteractionEventsRouter;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TheWorkMod implements ModInitializer {
    public static final String ModID = "thework";

    public static final Logger LOGGER = LoggerFactory.getLogger(TheWorkMod.class);

    private static final ArrayList<Runnable> scheduledEvents = new ArrayList<>();


    @Override
    public void onInitialize() {
        TheWorkElements.initialize();
        NodeTypes.initialize();

        TheWorkEntities.initialize();
        TheWorkItems.initialize();
        TheWorkBlocks.initialize();
        TheWorkBlockEntities.initialize();
        TheWorkNetworking.initialize();

        ServerTickEvents.START_SERVER_TICK.register(this::serverTickLogic);


        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(TheWorkMod.ModID, "custom_resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                TheWorkRecipes.loadRecipes(manager);
            }
        });
    }

    public static void schedule(Runnable runnable){
        synchronized (scheduledEvents){
            scheduledEvents.add(runnable);
        }
    }

    private void serverTickLogic(MinecraftServer server) {
        synchronized (scheduledEvents) {
            for (Runnable event : scheduledEvents) {
                try {
                    event.run();
                } catch (Exception e) {
                    LOGGER.error(e.toString());
                }
            }

            scheduledEvents.clear();
        }
    }

}