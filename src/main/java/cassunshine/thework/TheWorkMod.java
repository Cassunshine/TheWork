package cassunshine.thework;

import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeTypes;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.elements.TheWorkElements;
import cassunshine.thework.items.TheWorkItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheWorkMod implements ModInitializer {
    public static final String ModID = "thework";

    public static final Logger LOGGER = LoggerFactory.getLogger(TheWorkMod.class);

    @Override
    public void onInitialize() {
        TheWorkElements.initialize();
        NodeTypes.initialize();

        TheWorkItems.initialize();
        TheWorkBlocks.initialize();
        TheWorkBlockEntities.initialize();
    }
}