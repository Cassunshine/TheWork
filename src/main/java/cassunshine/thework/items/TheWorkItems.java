package cassunshine.thework.items;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.chemistry.ChemistryObject;
import cassunshine.thework.alchemy.chemistry.FurnaceBurnerChemistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class TheWorkItems {

    private static final HashMap<Class<?>, Identifier> CLASS_TO_ITEM_ID = new HashMap<>();

    public static final ChalkItem CHALK_ITEM = new ChalkItem();
    public static final AlchemistNotebookItem ALCHEMIST_NOTEBOOK_ITEM = new AlchemistNotebookItem();


    public static void initialize() {
        registerItem(CHALK_ITEM, "chalk", ItemGroups.TOOLS);
        registerItem(ALCHEMIST_NOTEBOOK_ITEM, "alchemist_notebook", ItemGroups.TOOLS);

        //registerItem(new ChemistryObjectItem(FurnaceBurnerChemistryObject.IDENTIFIER), FurnaceBurnerChemistryObject.IDENTIFIER);

        //CLASS_TO_ITEM_ID.put(FurnaceBurnerChemistryObject.class, FurnaceBurnerChemistryObject.IDENTIFIER);
    }


    public static void registerItem(Item item, String name, RegistryKey<ItemGroup> group) {
        Registry.register(Registries.ITEM, new Identifier(TheWorkMod.ModID, name), item);
        if(group != null)
            ItemGroupEvents.modifyEntriesEvent(group).register(c -> c.add(new ItemStack(item)));
    }

    public static void registerItem(Item item, Identifier id) {
        Registry.register(Registries.ITEM, id, item);
    }

    public static Item getItem(Object object) {
        var id = CLASS_TO_ITEM_ID.get(object);
        if (id == null)
            return null;

        return Registries.ITEM.get(id);
    }
}
