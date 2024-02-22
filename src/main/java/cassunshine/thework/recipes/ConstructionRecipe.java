package cassunshine.thework.recipes;

import cassunshine.thework.alchemy.elements.ElementPacket;
import net.minecraft.item.ItemStack;

public class ConstructionRecipe {

    public final ElementPacket[][] inputRings;

    public final ItemStack[] outputs;

    public final String signature;

    public ConstructionRecipe(ElementPacket[][] rings, ItemStack[] outputs, String signature) {
        this.inputRings = rings;
        this.outputs = outputs;
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "Construction " + signature;
    }
}
