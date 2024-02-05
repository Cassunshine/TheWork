package cassunshine.thework.blocks;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.TheWorkItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TheWorkBlocks {

    public static final Block ALCHEMY_CIRCLE_BLOCK = new AlchemyCircleBlock();


    public static void initialize() {
        registerBlock(ALCHEMY_CIRCLE_BLOCK, "alchemy_circle");
    }

    private static void registerBlock(Block b, String name) {
        Registry.register(Registries.BLOCK, new Identifier(TheWorkMod.ModID, name), b);
    }

    private static void registerBlockWithItem(Block b, String name) {
        registerBlockWithItem(b, name, new FabricItemSettings());
    }

    private static void registerBlockWithItem(Block b, String name, Item.Settings itemSettings) {
        registerBlock(b, name);
        TheWorkItems.registerItem(new BlockItem(b, itemSettings), name);
    }
}
