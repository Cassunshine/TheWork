package cassunshine.thework.items;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import cassunshine.thework.network.events.bookevents.DiscoverMechanicEvent;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AlchemyJarItem extends BlockItem {
    public AlchemyJarItem() {
        super(TheWorkBlocks.ALCHEMY_JAR_BLOCK, new FabricItemSettings().maxCount(1));
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        super.onCraftByPlayer(stack, world, player);

        TheWorkNetworkEvents.sendBookLearnEvent(player.getBlockPos(), world, new DiscoverMechanicEvent(new Identifier(TheWorkMod.ModID, "jar")));
    }
}
