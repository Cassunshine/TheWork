package cassunshine.thework.alchemy.circle.node.type;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blocks.TheWorkBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class AlchemyNodeTypes {

    private static HashMap<Identifier, AlchemyNodeType> NODE_TYPES = new HashMap<>();

    public static final AlchemyNodeType NONE = register(new Identifier(TheWorkMod.ModID, "none"), new AlchemyNodeType());

    public static final AlchemyNodeType DECONSTRUCT = register(new Identifier(TheWorkMod.ModID, "deconstruct"), new DeconstructNodeType().withItemHolding(i -> true));

    public static final AlchemyNodeType CONSTRUCT = register(new Identifier(TheWorkMod.ModID, "construct"), new ConstructNodeType());

    public static final AlchemyNodeType TRANSFER = register(new Identifier(TheWorkMod.ModID, "transfer"), new TransferNodeType().withItemHolding(i -> i.getItem() instanceof BlockItem bi && bi.getBlock() == TheWorkBlocks.ALCHEMY_JAR_BLOCK));


    public static void initialize() {

    }

    private static <T extends AlchemyNodeType> T register(Identifier id, T toRegister) {
        NODE_TYPES.put(id, toRegister);
        toRegister.id = id;
        return toRegister;
    }

    public static AlchemyNodeType get(Identifier identifier) {
        return NODE_TYPES.getOrDefault(identifier, NONE);
    }
}
