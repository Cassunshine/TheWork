package cassunshine.thework.items.notebook.sections;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.notebook.NotebookData;
import cassunshine.thework.items.notebook.pages.journal.JournalPage;
import com.google.common.collect.ImmutableMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

public class StorySection extends JournalSection {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "story");

    public static final ArrayList<Pair<Identifier, Predicate<NotebookData>>> autoDiscoveryList = new ArrayList<>();

    public StorySection(NotebookData data) {
        super(data, IDENTIFIER, new ItemStack(Items.WRITABLE_BOOK));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (!journalPages.isEmpty()) {
            journalPages.add(0, new Identifier(TheWorkMod.ModID, "oan/intro"));
        }
    }

    public void onItemDiscovered(Item discovered) {
        autoDiscover();
    }

    public void onRecipeObtained(Item recipeResult) {
        autoDiscover();
    }

    private void autoDiscover() {
        for (var pair : autoDiscoveryList) {
            var id = pair.getLeft();
            var predicate = pair.getRight();

            if (journalPages.contains(id))
                continue;

            if (predicate.test(data))
                journalPages.add(id);
        }
    }

    static {
        autoDiscoveryList.add(new Pair<>(new Identifier(TheWorkMod.ModID, "oan/jar"), d -> d.recipesSection.knownRecipes >= 3));
    }
}