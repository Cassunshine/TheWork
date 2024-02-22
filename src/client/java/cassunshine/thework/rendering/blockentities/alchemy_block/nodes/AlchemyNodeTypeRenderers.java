package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeType;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class AlchemyNodeTypeRenderers {
    private static final HashMap<AlchemyNodeType, AlchemyNodeTypeRenderer> renderers = new HashMap<>();

    public static void initialize() {

    }

    private static void register(AlchemyNodeType type, AlchemyNodeTypeRenderer renderer) {
        renderers.put(type, renderer);
    }

    public static AlchemyNodeTypeRenderer get(AlchemyNodeType type) {
        return renderers.get(type);
    }

    static {
        
    }
}