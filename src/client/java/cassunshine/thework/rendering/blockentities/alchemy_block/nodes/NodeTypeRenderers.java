package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.blockentities.alchemy_circle.nodes.ElementNodeType;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeType;

import java.util.HashMap;

public class NodeTypeRenderers {
    private static final HashMap<Class<?>, NodeTypeRenderer<?>> renderers = new HashMap<>();

    public static void initialize() {

    }

    private static void register(Class<?> type, NodeTypeRenderer<?> renderer) {
        renderers.put(type, renderer);
    }

    public static <T extends NodeType> NodeTypeRenderer<T> get(Class<?> type) {
        return (NodeTypeRenderer<T>) renderers.get(type); //yea yea
    }

    static {
        register(ElementNodeType.class, new ElementNodeRenderer());
    }
}
