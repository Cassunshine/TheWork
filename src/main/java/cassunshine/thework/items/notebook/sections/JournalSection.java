package cassunshine.thework.items.notebook.sections;

import cassunshine.thework.items.notebook.NotebookData;
import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;
import cassunshine.thework.items.notebook.pages.journal.JournalPage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

/**
 * Stores which journal entries have been discovered, in order.
 * Single journal entries might have multiple pages, so we don't store pages individually
 */
public class JournalSection extends AlchemistNotebookSection {

    /**
     * List of all journal entries.
     * A single journal entry can have multiple pages, but they're all lumped together when that happens.
     */
    public final ArrayList<Identifier> journalPages = new ArrayList<>();

    /**
     * List of pages the clients will add before adding the dynamic journal pages.
     */
    public final ArrayList<AlchemistNotebookPage> defaultPages = new ArrayList<>();

    public JournalSection(NotebookData data, Identifier id, ItemStack tabIcon) {
        super(data, id, tabIcon);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        var pagesList = new NbtList();
        nbt.put("pages", pagesList);

        for (Identifier page : journalPages)
            pagesList.add(NbtString.of(page.toString()));

        for (AlchemistNotebookPage page : defaultPages)
            nbt.put(page.id.toString(), page.writeNbt(new NbtCompound()));

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        journalPages.clear();
        pages.clear();
        journalPages.clear();

        var pagesList = nbt.getList("pages", NbtElement.STRING_TYPE);

        for (int i = 0; i < pagesList.size(); i++) {
            var id = new Identifier(pagesList.getString(i));
            journalPages.add(id);
            pages.add(new JournalPage(id));
        }

        for (AlchemistNotebookPage page : defaultPages) {
            var key = page.id.toString();
            if (nbt.contains(key, NbtElement.COMPOUND_TYPE))
                page.readNbt(nbt.getCompound(key));
        }
    }
}
