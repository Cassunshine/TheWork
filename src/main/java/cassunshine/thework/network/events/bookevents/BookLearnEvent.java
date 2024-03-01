package cassunshine.thework.network.events.bookevents;

import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.items.notebook.NotebookData;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

/**
 * Base class used for all events in the game that should cause the Alchemist's Notebook to discover something new.
 */
public abstract class BookLearnEvent extends TheWorkNetworkEvent {

    public BookLearnEvent(Identifier id) {
        super(id);
    }


    public void applyToPlayer(PlayerEntity player) {
        var inv = player.getInventory();

        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getStack(i);

            if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM))
                continue;

            var data = NotebookData.getData(stack.getOrCreateNbt());
            var ret = applyToNotebook(data);

            stack.setNbt(data.writeNbt(new NbtCompound()));

            if (ret) {
                var world = player.getWorld();

                if (world.isClient)
                    world.playSound(player, player.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1, 1);
            }
        }

    }

    public boolean applyToNotebook(NotebookData data) {
        return false;
    }
}
