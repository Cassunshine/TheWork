package cassunshine.thework.alchemy.circle.node.type;

import cassunshine.thework.blocks.TheWorkBlocks;
import net.minecraft.item.BlockItem;

public class AlchemyNodeTypes {

    private static final AlchemyNodeType[] NODE_TYPES;

    public static final AlchemyNodeType NONE = new AlchemyNodeType();

    public static final AlchemyNodeType DECONSTRUCT = new DeconstructNodeType().withItemHolding(i -> true);
    public static final AlchemyNodeType RESEARCH = new ResearchNodeType();

    public static final AlchemyNodeType CONSTRUCT = new ConstructNodeType();

    public static final AlchemyNodeType TRANSFER = new TransferNodeType().withItemHolding(i -> i.getItem() instanceof BlockItem bi && bi.getBlock() == TheWorkBlocks.ALCHEMY_JAR_BLOCK);

    static {
        NODE_TYPES = new AlchemyNodeType[]{
                NONE,       //0
                NONE,       //1
                NONE,       //2
                TRANSFER,   //3
                DECONSTRUCT,       //4
                RESEARCH,       //5
                CONSTRUCT,       //6
                NONE,       //7
                NONE,       //8
        };
    }

    public static void initialize() {

    }

    /**
     * Gets the type of node based on the number of sides it has.
     */
    public static AlchemyNodeType get(int sides) {
        return NODE_TYPES[sides];
    }
}
