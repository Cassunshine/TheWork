package cassunshine.thework.network.events.bookevents;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.items.notebook.NotebookData;
import cassunshine.thework.network.events.WitnessEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class WitnessRecipeEvent extends WitnessEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "witness_recipe");

    public Item witnessedItem;

    public WitnessRecipeEvent() {
        super(IDENTIFIER);
    }

    public WitnessRecipeEvent(BlockPos pos, Item witnessedItem) {
        super(pos, IDENTIFIER);

        this.witnessedItem = witnessedItem;
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeInt(Registries.ITEM.getRawId(witnessedItem));
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        witnessedItem = Registries.ITEM.get(buf.readInt());
    }

    @Override
    public void applyToPlayer(PlayerEntity e) {
        super.applyToPlayer(e);

        var inv = e.getInventory();

        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getStack(i);
            if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM))
                continue;

            var data = NotebookData.getData(stack.getOrCreateNbt());
            data.recipesSection.cheatRecipe(witnessedItem);
            stack.setNbt(data.writeNbt(new NbtCompound()));
        }
    }
}
