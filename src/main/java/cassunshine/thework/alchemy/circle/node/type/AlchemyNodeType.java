package cassunshine.thework.alchemy.circle.node.type;

import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public class AlchemyNodeType {
    public Identifier id;

    public Predicate<ItemStack> heldItemFilter = null;

    public void activate(AlchemyNode node) {

    }

    public void activeTick(AlchemyNode node) {
        //Default behaviour for a node type is to simply move the entire inventory into the link.
        node.inventory.transfer(node.linkOutput, Float.POSITIVE_INFINITY);
    }

    public void deactivate(AlchemyNode node) {

    }

    public NbtCompound getDefaultData() {
        return new NbtCompound();
    }

    public AlchemyNodeType withItemHolding(Predicate<ItemStack> filter) {
        heldItemFilter = filter;
        return this;
    }
}
