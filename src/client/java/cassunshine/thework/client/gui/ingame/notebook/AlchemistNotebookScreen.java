package cassunshine.thework.client.gui.ingame.notebook;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.function.Supplier;

public abstract class AlchemistNotebookScreen extends Screen {
    public NbtCompound itemNbt;
    public NbtCompound pageNbt;

    public AlchemistNotebookScreen(ItemStack stack, Text title, String pageName) {
        super(title);

        itemNbt = stack.getOrCreateNbt();
        this.client = MinecraftClient.getInstance();

        pageNbt = itemNbt.getCompound(pageName);
        itemNbt.put(pageName, pageNbt);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
