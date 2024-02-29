package cassunshine.thework.items.notebook;

import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;
import cassunshine.thework.items.notebook.sections.AlchemistNotebookSection;
import cassunshine.thework.items.notebook.sections.MechanicsSection;
import cassunshine.thework.items.notebook.sections.recipe.RecipesSection;
import cassunshine.thework.items.notebook.sections.StorySection;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Pair;

public class NotebookData {
    private static final ThreadLocal<NotebookData> THREAD_LOCAL_DATA = new ThreadLocal<>();

    public int currentSection;
    public int currentPage;

    public final ImmutableList<AlchemistNotebookSection> sections;

    public final MechanicsSection mechanicsSection;
    public final RecipesSection recipesSection;
    public final StorySection storySection;

    public NotebookData() {
        currentSection = 0;
        currentPage = 0;

        //Sections
        {
            var builder = new ImmutableList.Builder<AlchemistNotebookSection>();

            builder.add(mechanicsSection = new MechanicsSection(this));
            builder.add(recipesSection = new RecipesSection(this));
            builder.add(storySection = new StorySection(this));

            sections = builder.build();
        }
    }

    public static NotebookData getData(NbtCompound compound) {
        var existing = THREAD_LOCAL_DATA.get();
        if (existing == null)
            THREAD_LOCAL_DATA.set(existing = new NotebookData());

        existing.readNbt(compound);
        return existing;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {

        nbt.putInt("current_section", currentSection);
        nbt.putInt("current_page", currentPage);

        NbtCompound sectionsNbt = new NbtCompound();
        for (AlchemistNotebookSection section : sections)
            sectionsNbt.put(section.identifier.toString(), section.writeNbt(new NbtCompound()));

        nbt.put("sections", sectionsNbt);

        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        currentSection = nbt.getInt("current_section");
        currentPage = nbt.getInt("current_page");

        NbtCompound sectionsNbt = nbt.getCompound("sections");
        for (AlchemistNotebookSection section : sections) {
            var key = section.identifier.toString();
            //if (sectionsNbt.contains(key, NbtElement.COMPOUND_TYPE))
            section.readNbt(sectionsNbt.getCompound(key));
        }
    }

    public AlchemistNotebookSection getCurrentSection() {
        return sections.get(currentSection);
    }

    public Pair<AlchemistNotebookPage, AlchemistNotebookPage> getPages() {
        return getCurrentSection().getPages(currentPage);
    }

    public void onItemDiscovered(Item discovered) {

    }
}
