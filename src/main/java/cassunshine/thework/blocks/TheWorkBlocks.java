package cassunshine.thework.blocks;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.TheWorkItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class TheWorkBlocks {

    public static final Block ALCHEMY_CIRCLE_BLOCK = new AlchemyCircleBlock();
    public static final Block CHEMISTRY_SET_BLOCK = new ChemistrySetBlock();

    public static final Block ALCHEMY_JAR_BLOCK = new AlchemyJarBlock();


    public static void initialize() {
        registerBlock(ALCHEMY_CIRCLE_BLOCK, "alchemy_circle");
        registerBlock(CHEMISTRY_SET_BLOCK, "chemistry_set");

        registerBlockWithItem(ALCHEMY_JAR_BLOCK, "alchemy_jar", ItemGroups.FUNCTIONAL);
    }

    private static void registerBlock(Block b, String name) {
        Registry.register(Registries.BLOCK, new Identifier(TheWorkMod.ModID, name), b);
    }

    private static void registerBlockWithItem(Block b, String name, RegistryKey<ItemGroup> group) {
        registerBlockWithItem(b, name, new FabricItemSettings(), group);

    }

    private static void registerBlockWithItem(Block b, String name, Item.Settings itemSettings, RegistryKey<ItemGroup> group) {
        registerBlock(b, name);
        TheWorkItems.registerItem(new BlockItem(b, itemSettings), name, group);
    }
}
