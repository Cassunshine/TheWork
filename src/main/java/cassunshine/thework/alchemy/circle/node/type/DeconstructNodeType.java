package cassunshine.thework.alchemy.circle.node.type;

import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.recipes.TheWorkRecipes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;

public class DeconstructNodeType extends AlchemyNodeType {

    @Override
    public void activate(AlchemyNode node) {
        super.activate(node);

        if (!(node.typeData instanceof DeconstructNodeData data)) return;

        data.cooldown = 0;
    }

    @Override
    public void activeTick(AlchemyNode node) {
        super.activeTick(node);

        if (!(node.typeData instanceof DeconstructNodeData data)) return;

        if (data.cooldown > 0) {
            data.cooldown--;
            return;
        }

        if (node.heldStack.isEmpty()) {
            data.cooldown = 20;
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

        //Add elements to inventory now that its destroyed.
        for (int i = 0; i < recipe.output().length; i++) {
            var output = recipe.output()[i];
            node.inventory.put(output.element(), output.amount());
        }

        //Set on cooldown.
        data.cooldown = recipe.time();
    }

    @Override
    public Data getData() {
        return new DeconstructNodeData();
    }

    public static class DeconstructNodeData extends Data {

        public int cooldown;

        @Override
        public NbtCompound writeNbt(NbtCompound nbt) {
            nbt.putInt("cooldown", cooldown);
            return nbt;
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            cooldown = nbt.getInt("cooldown");
        }
    }
}