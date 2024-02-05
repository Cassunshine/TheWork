package cassunshine.thework;

import cassunshine.thework.rendering.blockentities.TheWorkBlockEntityRenderers;
import cassunshine.thework.rendering.blockentities.alchemy_block.nodes.NodeTypeRenderers;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheWorkClient implements ClientModInitializer {
    public static final Logger CLIENT_LOGGER = LoggerFactory.getLogger(TheWorkClient.class);

    @Override
    public void onInitializeClient() {
        TheWorkBlockEntityRenderers.initialize();
        NodeTypeRenderers.initialize();
    }
}