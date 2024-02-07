package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.blockentities.alchemy_circle.nodes.types.AlchemyNodeType;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.AlchemyNodeTypes;

import java.util.HashMap;

public class AlchemyNodeTypeRenderers {
    private static final HashMap<AlchemyNodeType, AlchemyNodeTypeRenderer> renderers = new HashMap<>();

    public static void initialize() {

    }

    private static void register(AlchemyNodeType type, AlchemyNodeTypeRenderer renderer) {
        renderers.put(type, renderer);
    }

    public static <T extends AlchemyNodeType> AlchemyNodeTypeRenderer get(AlchemyNodeType type) {
        return renderers.get(type);
    }

    static {
        register(AlchemyNodeTypes.DECONSTRUCT, new RuneAlchemyNodeTypeRenderer().withSides(4));
        register(AlchemyNodeTypes.CONSTRUCT, new RuneAlchemyNodeTypeRenderer().withSides(6));
    }
}
