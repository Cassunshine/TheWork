package cassunshine.thework.assets.elements;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class IconElement extends JournalLayoutElement {
    public ItemStack iconStack;

    public static IconElement fromJson(JsonObject object) {
        var element = new IconElement();
        apply(element, object);

        if (object.has("contents"))
            //I hate this line so much lol
            element.iconStack = new ItemStack(Registries.ITEM.get(new Identifier(object.get("contents").getAsString())));

        return element;
    }
}
