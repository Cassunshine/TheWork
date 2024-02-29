package cassunshine.thework.items.notebook.sections;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.notebook.NotebookData;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class StorySection extends AlchemistNotebookSection {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "story");

    public StorySection(NotebookData data) {
        super(data, IDENTIFIER, new ItemStack(Items.WRITABLE_BOOK));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {

    }
}
