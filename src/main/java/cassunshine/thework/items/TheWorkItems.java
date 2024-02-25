package cassunshine.thework.items;

import cassunshine.thework.TheWorkMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.HashMap;

public class TheWorkItems {

    private static final HashMap<Class<?>, Identifier> CLASS_TO_ITEM_ID = new HashMap<>();

    public static final ChalkItem WHITE_CHALK = new ChalkItem(0xFFFFFFFF);
    public static final ChalkItem GRAY_CHALK = new ChalkItem(0xFF9D9D97);
    public static final ChalkItem BLACK_CHALK = new ChalkItem(0xFF1D1D21);
    public static final ChalkItem BROWN_CHALK = new ChalkItem(0xFF835432);
    public static final ChalkItem RED_CHALK = new ChalkItem(0xFFB02E26);
    public static final ChalkItem ORANGE_CHALK = new ChalkItem(0xFFF9801D);
    public static final ChalkItem YELLOW_CHALK = new ChalkItem(0xFFFED83D);
    public static final ChalkItem GREEN_CHALK = new ChalkItem(0xFF80C71F);
    public static final ChalkItem BLUE_CHALK = new ChalkItem(0xFF3C44AA);
    public static final ChalkItem PURPLE_CHALK = new ChalkItem(0xFF8932B8);
    public static final ChalkItem PINK_CHALK = new ChalkItem(0xFFF38BAA);


    public static final AlchemistNotebookItem ALCHEMIST_NOTEBOOK_ITEM = new AlchemistNotebookItem();

    public static void initialize() {
        registerItem(WHITE_CHALK, "chalk", ItemGroups.TOOLS);
        registerItem(GRAY_CHALK, "gray_chalk", ItemGroups.TOOLS);
        registerItem(BLACK_CHALK, "black_chalk", ItemGroups.TOOLS);
        registerItem(BROWN_CHALK, "brown_chalk", ItemGroups.TOOLS);
        registerItem(RED_CHALK, "red_chalk", ItemGroups.TOOLS);
        registerItem(ORANGE_CHALK, "orange_chalk", ItemGroups.TOOLS);
        registerItem(YELLOW_CHALK, "yellow_chalk", ItemGroups.TOOLS);
        registerItem(GREEN_CHALK, "green_chalk", ItemGroups.TOOLS);
        registerItem(BLUE_CHALK, "blue_chalk", ItemGroups.TOOLS);
        registerItem(PURPLE_CHALK, "purple_chalk", ItemGroups.TOOLS);
        registerItem(PINK_CHALK, "pink_chalk", ItemGroups.TOOLS);


        registerItem(ALCHEMIST_NOTEBOOK_ITEM, "alchemist_notebook", ItemGroups.TOOLS);

        //registerItem(new ChemistryObjectItem(FurnaceBurnerChemistryObject.IDENTIFIER), FurnaceBurnerChemistryObject.IDENTIFIER);

        //CLASS_TO_ITEM_ID.put(FurnaceBurnerChemistryObject.class, FurnaceBurnerChemistryObject.IDENTIFIER);
    }


    public static void registerItem(Item item, String name, RegistryKey<ItemGroup> group) {
        Registry.register(Registries.ITEM, new Identifier(TheWorkMod.ModID, name), item);
        if (group != null)
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
