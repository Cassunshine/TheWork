package cassunshine.thework.items.notebook.sections;

import cassunshine.thework.items.notebook.NotebookData;
import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;

/**
 * Holds and generates pages for an alchemist notebook based on both other sections and
 */
public abstract class AlchemistNotebookSection {

    public final NotebookData data;
    public final Identifier identifier;
    public final ItemStack tabIcon;

    public final ArrayList<AlchemistNotebookPage> pages = new ArrayList<>();


    public AlchemistNotebookSection(NotebookData data, Identifier id, ItemStack tabIcon) {
        this.data = data;
        this.identifier = id;
        this.tabIcon = tabIcon;
    }

    public Pair<AlchemistNotebookPage, AlchemistNotebookPage> getPages(int index) {
        int rightIndex = index + 1;

        AlchemistNotebookPage leftPage = null;
        AlchemistNotebookPage rightPage = null;

        if (index < pages.size())
            leftPage = pages.get(index);
        if (rightIndex < pages.size())
            rightPage = pages.get(rightIndex);

        return new Pair<>(leftPage, rightPage);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        for (AlchemistNotebookPage page : pages)
            nbt.put(page.id.toString(), page.writeNbt(new NbtCompound()));

        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        for (AlchemistNotebookPage page : pages) {
            var key = page.id.toString();
            if (nbt.contains(key, NbtElement.COMPOUND_TYPE))
                page.readNbt(nbt.getCompound(key));
        }
    }
}