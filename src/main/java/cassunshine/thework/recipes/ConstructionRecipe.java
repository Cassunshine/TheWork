package cassunshine.thework.recipes;

import cassunshine.thework.elements.Element;
import cassunshine.thework.elements.ElementPacket;
import cassunshine.thework.utils.TheWorkUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

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
