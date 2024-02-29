package cassunshine.thework.items.notebook.pages;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public abstract class AlchemistNotebookPage {

    public static final int PAGE_SCALE = 3;
    public static final int PAGE_WIDTH = 48 * PAGE_SCALE;
    public static final int PAGE_HEIGHT = 64 * PAGE_SCALE;

    public final Identifier id;

    protected AlchemistNotebookPage(Identifier id) {
        this.id = id;
    }

    public abstract NbtCompound writeNbt(NbtCompound nbt);

    public abstract void readNbt(NbtCompound nbt);
}
