package cassunshine.thework.items;

import cassunshine.thework.TheWorkMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TheWorkItems {

    public static final ChalkItem CHALK_ITEM = new ChalkItem();

    public static void initialize() {
        registerItem(CHALK_ITEM, "chalk");
    }


    public static void registerItem(Item item, String name) {
        Registry.register(Registries.ITEM, new Identifier(TheWorkMod.ModID, name), item);
    }
}
