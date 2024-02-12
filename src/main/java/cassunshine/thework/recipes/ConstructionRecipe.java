package cassunshine.thework.recipes;

import cassunshine.thework.elements.Element;
import cassunshine.thework.utils.TheWorkUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ConstructionRecipe {

    public final Entry[][] inputRings;

    public final ItemStack[] outputs;

    public final String signature;

    public ConstructionRecipe(Entry[][] rings, ItemStack[] outputs, String signature) {
        this.inputRings = rings;
        this.outputs = outputs;
        this.signature = signature;
    }


    public static class Entry {
        public final Element element;
        public final int amount;

        public Entry(Element element, int amount) {
            this.element = element;
            this.amount = amount;
        }
    }
}
