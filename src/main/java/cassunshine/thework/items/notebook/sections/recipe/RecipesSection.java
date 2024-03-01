package cassunshine.thework.items.notebook.sections.recipe;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.notebook.NotebookData;
import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;
import cassunshine.thework.items.notebook.pages.RecipePage;
import cassunshine.thework.items.notebook.sections.AlchemistNotebookSection;
import cassunshine.thework.data.recipes.TheWorkRecipes;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class RecipesSection extends AlchemistNotebookSection {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "recipes");

    public int knownRecipes = 0;

    public RecipesSection(NotebookData data) {
        super(data, IDENTIFIER, new ItemStack(Blocks.GRASS_BLOCK.asItem()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        //Clear any previous pages
        pages.clear();

        //For each NBT entry
        for (String key : nbt.getKeys()) {
            //Get the recipe that would be for this entry, if any.
            var id = new Identifier(key);
            var item = Registries.ITEM.get(id);
            var recipe = TheWorkRecipes.getConstruction(item);

            //If there is no recipe, skip it.
            if (recipe == null)
                continue;

            //Add a page for that recipe.
            pages.add(new RecipePage(this, id));
        }

        //Read page data.
        super.readNbt(nbt);

        for (AlchemistNotebookPage page : pages) {
            if (!(page instanceof RecipePage recipePage))
                continue;

            if (recipePage.isCorrect)
                knownRecipes++;
        }
    }

    public boolean knowsItem(Item item) {
        return getPage(item) != null;
    }

    public RecipePage getPage(Item item) {
        var id = Registries.ITEM.getId(item);

        for (AlchemistNotebookPage page : pages)
            if (page instanceof RecipePage recipePage && recipePage.id.equals(id))
                return recipePage;

        return null;
    }

    private RecipePage putRecipePageIfNew(Item item) {
        var id = Registries.ITEM.getId(item);
        var recipe = TheWorkRecipes.getConstruction(item);
        var page = getPage(item);

        if (recipe == null || page != null)
            return null;

        var newPage = new RecipePage(this, id);

        pages.add(newPage);

        data.currentSection = data.sections.indexOf(this);
        data.currentPage = 0;

        data.onItemDiscovered(item);
        return newPage;
    }

    public ActionResult putItemIfNew(Item item) {
        return putRecipePageIfNew(item) == null ? ActionResult.PASS : ActionResult.SUCCESS;
    }

    public void cheatRecipe(Item item) {
        //Put recipe into book
        putItemIfNew(item);

        //If no recipe exists, fail.
        var page = getPage(item);
        if (page == null)
            return;

        page.cheatRecipe();
    }

    public void onRecipeObtained(Item item) {
        knownRecipes = 0;
        for (AlchemistNotebookPage page : pages) {
            if (!(page instanceof RecipePage recipePage))
                continue;

            if (recipePage.isCorrect)
                knownRecipes++;
        }

        data.onRecipeObtained(item);
    }
}
