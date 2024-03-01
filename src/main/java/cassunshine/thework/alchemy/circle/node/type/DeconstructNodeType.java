package cassunshine.thework.alchemy.circle.node.type;

import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.blocks.AlchemyJarBlock;
import cassunshine.thework.data.recipes.TheWorkRecipes;
import net.minecraft.registry.Registries;

public class DeconstructNodeType extends AlchemyNodeType {

    @Override
    public void activate(AlchemyNode node) {
        super.activate(node);

        node.typeData.putInt("cooldown", 20);
    }

    @Override
    public void activeTick(AlchemyNode node) {
        super.activeTick(node);

        var data = node.typeData;

        if (data.getInt("cooldown") > 0) {
            data.putInt("cooldown", data.getInt("cooldown") - 1);
            return;
        }

        if (node.heldStack.isEmpty()) {
            data.putInt("cooldown", Integer.MAX_VALUE);
            return;
        }

        var jarInv = AlchemyJarBlock.getInventoryForStack(node.heldStack);
        if (jarInv != null) {
            jarInv.transferSingle(node.inventory, 1);
            return;
        }

        var itemId = Registries.ITEM.getId(node.heldStack.getItem());
        var recipe = TheWorkRecipes.getDeconstruction(itemId);

        if (recipe == null)
            return;

        //If inventory can't fit any of the elements, don't deconstruct.
        for (int i = 0; i < recipe.output().length; i++) {
            var output = recipe.output()[i];
            if (!node.inventory.canFit(output.element(), output.amount())) return;
        }

        //Destroy item.
        node.heldStack.decrement(1);

        var outputs = recipe.output();
        var movedOutputs = 0.0f;

        //Add elements to the output now that the item is destroyed.
        for (int i = 0; i < outputs.length; i++) {
            var output = outputs[i];
            node.linkOutput.put(output.element(), output.amount());
            movedOutputs += output.amount();
        }

        //Set on cooldown.
        data.putInt("cooldown", Math.max(recipe.time(), (int) Math.floor(movedOutputs)));
    }
}