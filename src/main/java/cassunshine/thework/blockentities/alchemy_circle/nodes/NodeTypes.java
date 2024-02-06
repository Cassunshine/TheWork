package cassunshine.thework.blockentities.alchemy_circle.nodes;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.elements.TheWorkElements;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class NodeTypes {

    private static final HashMap<Identifier, NodeType> types = new HashMap<>();

    public static final NodeType NONE = registerType("none", new NodeType());
    public static final NodeType TEST = registerType("test", new NodeType());
    public static final NodeType ITEM = registerType("item", new ItemNodeType());

    public static void initialize() {
        for (var kvp : TheWorkElements.ELEMENT_HASH_MAP.entrySet()) {
            registerType(kvp.getKey().getPath(), new ElementNodeType(kvp.getValue()));
        }
    }

    private static <T extends NodeType> T registerType(String name, T type) {
        var id = new Identifier(TheWorkMod.ModID, name);
        type.id = id;
        types.put(id, type);
        return type;
    }

    public static NodeType getType(Identifier id) {
        var t = types.get(id);

        return t == null ? NONE : t;
    }
}
