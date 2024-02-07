package cassunshine.thework.elements.recipes;

import cassunshine.thework.elements.Element;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.Identifier;

public record ConstructionRecipe(Ring[] inputs, Object2IntMap<Identifier> outputs) {
    public record Ring(Entry[] entries) {
        public record Entry(Element element, float amount) {
        }
    }


    public static String buildSignature(ConstructionRecipe recipe) {
        StringBuilder builder = new StringBuilder();

        for (Ring input : recipe.inputs) {
            builder.append('[');

            for (Ring.Entry entry : input.entries) {
                builder.append(entry.element);
                builder.append(';');
            }

            builder.append(']');
        }

        return builder.toString();
    }
}
