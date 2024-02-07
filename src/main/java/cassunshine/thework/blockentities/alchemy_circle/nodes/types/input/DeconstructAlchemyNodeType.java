package cassunshine.thework.blockentities.alchemy_circle.nodes.types.input;

import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.AlchemyNodeType;
import cassunshine.thework.blocks.AlchemyJarBlock;
import cassunshine.thework.elements.ElementPacket;
import cassunshine.thework.elements.Elements;
import cassunshine.thework.elements.inventory.ElementInventory;
import cassunshine.thework.elements.recipes.TheWorkRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class DeconstructAlchemyNodeType extends AlchemyNodeType {

    public DeconstructAlchemyNodeType() {
        requireInteractionEntity = true;
    }

    //Validity for deconstruction nodes is that they have an element assigned to them, or hold a non-empty item.
    //If the item is a jar that's empty, that's also invalid.
    @Override
    public boolean validityCheck(AlchemyNode node) {
        boolean hasNonEmptyItem = !AlchemyJarBlock.isEmptyOrEmptyJar(node.item);
        boolean hasRune = Elements.getElement(node.rune) != Elements.NONE;

        //Exclusive OR
        //We want EITHER an item OR a rune, not both.
        //Empty jar + rune is fine though.
        return hasNonEmptyItem ^ hasRune;
    }

    @Override
    public void operate(AlchemyNode node) {

        //Deconstruct item.
        if (node.cooldown <= 0 && !AlchemyJarBlock.isEmptyOrEmptyJar(node.item)) {
            node.cooldown = 20;

            deconstruct(node.item, node.inventory);
        }


        super.operate(node);
    }


    public boolean deconstruct(ItemStack stack, ElementInventory target) {
        if (stack.isEmpty())
            return false;

        var item = stack.getItem();
        var id = Registries.ITEM.getId(item);
        var recipe = TheWorkRecipes.getDeconstruction(id);

        //TODO - jar check!

        //Cannot deconstruct item, fail.
        if (recipe == null)
            return false;

        //Reduce stack count.
        stack.decrement(1);

        for (ElementPacket packet : recipe.output()) {
            target.add(packet.element(), packet.amount());
        }

        return true;
    }
}
