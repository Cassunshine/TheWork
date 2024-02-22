package cassunshine.thework.alchemy.circle;

import cassunshine.thework.alchemy.circle.layout.AlchemyCircleLayout;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.recipes.ConstructionRecipe;
import cassunshine.thework.recipes.TheWorkRecipes;
import cassunshine.thework.utils.TheWorkUtils;
import net.minecraft.item.ItemStack;

public class AlchemyCircleConstructLayout extends AlchemyCircleLayout {

    /**
     * The recipe for this layout, if any.
     */
    public final ConstructionRecipe recipe;


    public AlchemyCircleConstructLayout(AlchemyCircle circle) {
        super(circle, n -> n.nodeType == AlchemyNodeTypes.CONSTRUCT);
        recipe = TheWorkRecipes.getConstruction(fullSignature);
    }

    public void tryProduceItem() {

        //Check if all nodes have all resources.
        boolean hasAll = true;
        for (int i = 0; i < rings.size() && hasAll; i++) {
            var alchemyRing = rings.get(i);
            var recipeRing = recipe.inputRings[i];

            for (int n = 0; n < alchemyRing.nodes.size() && hasAll; n++) {
                var alchemyNode = alchemyRing.nodes.get(n);
                var recipeNode = recipeRing[n];

                hasAll = alchemyNode.inventory.has(recipeNode.element(), recipeNode.amount());
            }
        }

        //If they don't, do nothing.
        if (!hasAll)
            return;

        //Reduce resources.
        for (int i = 0; i < rings.size(); i++) {
            var alchemyRing = rings.get(i);
            var recipeRing = recipe.inputRings[i];

            for (int n = 0; n < alchemyRing.nodes.size(); n++) {
                var alchemyNode = alchemyRing.nodes.get(n);
                var recipeNode = recipeRing[n];

                alchemyNode.inventory.give(recipeNode.element(), recipeNode.amount());
            }
        }


        var centerPos = circle.blockEntity.flatPosition.add(0, circle.blockEntity.getPos().getY() + 1, 0);

        for (ItemStack output : recipe.outputs) {
            TheWorkUtils.dropItem(circle.blockEntity.getWorld(), output.copy(), centerPos.x, centerPos.y, centerPos.z);
        }
    }
}
