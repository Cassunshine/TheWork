package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.blockentities.alchemy_circle.nodes.ElementNodeType;
import cassunshine.thework.blockentities.alchemy_circle.nodes.ItemNodeType;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeType;

import java.util.HashMap;

public class NodeTypeRenderers {
    private static final HashMap<Class<?>, NodeRenderer> renderers = new HashMap<>();

    public static void initialize() {

    }

    private static void register(Class<?> type, NodeRenderer renderer) {
        renderers.put(type, renderer);
    }

    public static <T extends NodeType> NodeRenderer get(Class<?> type) {
        return renderers.get(type); //yea yea
    }

    static {
        register(ElementNodeType.class, new ElementNodeRenderer());
        register(ItemNodeType.class, new ItemNodeRenderer());
    }
}
