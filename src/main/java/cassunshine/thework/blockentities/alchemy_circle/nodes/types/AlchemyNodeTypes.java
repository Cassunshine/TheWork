package cassunshine.thework.blockentities.alchemy_circle.nodes.types;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.input.DeconstructAlchemyNodeType;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.work.ConstructAlchemyNodeType;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class AlchemyNodeTypes {
    private static final HashMap<Identifier, AlchemyNodeType> NODE_TYPES = new HashMap<>();

    public static final AlchemyNodeType NONE = register(new Identifier(TheWorkMod.ModID, "none"), new AlchemyNodeType());

    public static final AlchemyNodeType DECONSTRUCT = register(new Identifier(TheWorkMod.ModID, "deconstruct"), new DeconstructAlchemyNodeType());

    public static final AlchemyNodeType CONSTRUCT = register(new Identifier(TheWorkMod.ModID, "construct"), new ConstructAlchemyNodeType());


    public static void initialize() {

    }

    public static <T extends AlchemyNodeType> T register(Identifier id, T type) {
        NODE_TYPES.put(id, type);
        type.id = id;
        return type;
    }

    public static AlchemyNodeType getType(Identifier id) {
        return NODE_TYPES.getOrDefault(id, NONE);
    }
}
