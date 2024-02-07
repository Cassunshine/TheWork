package cassunshine.thework.blockentities.alchemy_circle.layout;

import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.AlchemyNodeType;
import cassunshine.thework.blockentities.alchemy_circle.rings.AlchemyRing;
import cassunshine.thework.elements.recipes.ConstructionRecipe;
import cassunshine.thework.elements.recipes.TheWorkRecipes;

import java.util.ArrayList;

public class AlchemyCircleLayout {

    public final ArrayList<ArrayList<AlchemyNode>> rings = new ArrayList<>();
    public final String signature;

    public AlchemyCircleLayout(AlchemyNodeType type, AlchemyCircleBlockEntity circle) {
        StringBuilder builder = new StringBuilder();

        for (AlchemyRing ring : circle.rings) {
            ArrayList<AlchemyNode> nodeList = null;

            for (AlchemyNode node : ring.nodes) {
                if (node.type != type)
                    continue;

                if (nodeList == null) {
                    nodeList = new ArrayList<>();
                    rings.add(nodeList);

                    builder.append('[');
                }
                nodeList.add(node);

                builder.append(node.rune);
                builder.append(';');
            }

            if (nodeList != null)
                builder.append(']');
        }

        signature = builder.toString();
    }

    public ConstructionRecipe recipe() {
        return TheWorkRecipes.getConstruction(signature);
    }
}
