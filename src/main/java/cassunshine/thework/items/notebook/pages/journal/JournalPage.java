package cassunshine.thework.items.notebook.pages.journal;

import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * These pages simply contain text, and sometimes icons for stuff.
 */
public class JournalPage extends AlchemistNotebookPage {

    public JournalPage(Identifier id) {
        super(id);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {

    }
}
