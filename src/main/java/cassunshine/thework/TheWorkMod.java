package cassunshine.thework;

import cassunshine.thework.alchemy.chemistry.ChemistryObjects;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.elements.Elements;
import cassunshine.thework.recipes.TheWorkRecipes;
import cassunshine.thework.entities.TheWorkEntities;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.network.TheWorkNetworking;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TheWorkMod implements ModInitializer {
    public static final String ModID = "thework";

    public static final Logger LOGGER = LoggerFactory.getLogger(TheWorkMod.class);

    private static final ArrayList<Runnable> scheduledEvents = new ArrayList<>();


    @Override
    public void onInitialize() {
        Elements.initialize();
        AlchemyNodeTypes.initialize();
        ChemistryObjects.initialize();

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

    public static void schedule(Runnable runnable) {
        synchronized (scheduledEvents) {
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