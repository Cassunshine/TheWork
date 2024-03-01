package cassunshine.thework.items.notebook.pages;

import cassunshine.thework.alchemy.elements.ElementPacket;
import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.data.recipes.ConstructionRecipe;
import cassunshine.thework.data.recipes.TheWorkRecipes;
import cassunshine.thework.items.notebook.sections.recipe.RecipesSection;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

/**
 * This page holds the recipe for a single item, and the guesses the player has made for its recipe.
 * Players automatically know how many rings the recipe will take.
 */
public class RecipePage extends AlchemistNotebookPage {

    public final RecipesSection section;

    public final ConstructionRecipe recipe;

    public final ArrayList<RingGuess> circleGuess = new ArrayList<>();

    public final Identifier itemID;

    public final ItemStack recipeOutputStack;

    public boolean isCorrect;


    /**
     * Constructs the page using an item's ID and the NBT data (if any) for the guesses on this page.
     */
    public RecipePage(RecipesSection section, Identifier itemID) {
        super(itemID);
        this.section = section;
        this.itemID = itemID;

        var item = Registries.ITEM.get(itemID);
        this.recipe = TheWorkRecipes.getConstruction(item);

        if (recipe == null)
            throw new RuntimeException("Unable to find recipe for item " + itemID.toString());

        float minRadius = 1.5f;

        //Fill out the ring guesses based on the recipe
        for (ElementPacket[] ring : recipe.inputRings) {
            var ringGuess = new RingGuess(minRadius++);
            for (ElementPacket elementPacket : ring)
                ringGuess.runeGuesses.add(TheWorkRunes.NULL);

            circleGuess.add(ringGuess);
        }

        recipeOutputStack = new ItemStack(item);
    }

    public void checkIfCorrect() {
        checkIfCorrect(false);
    }

    public void checkIfCorrect(boolean alert) {
        if (isCorrect)
            return;

        //TODO - replace with signature check
        for (int i = 0; i < recipe.inputRings.length; i++) {
            var ring = circleGuess.get(i);
            var ringRecipe = recipe.inputRings[i];

            for (int j = 0; j < ringRecipe.length; j++)
                if (!ring.runeGuesses.get(j).equals(ringRecipe[j].element().id))
                    return;
        }

        //If everything was correct, then we're good to go.
        isCorrect = true;
        if (alert)
            section.onRecipeObtained(recipeOutputStack.getItem());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        var circleGuess = new NbtList();
        for (RingGuess guess : this.circleGuess) {
            var ringGuess = new NbtList();
            circleGuess.add(ringGuess);

            for (Identifier runeGuess : guess.runeGuesses)
                ringGuess.add(NbtString.of(runeGuess.toString()));
        }

        nbt.put("guess", circleGuess);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        var circleGuess = nbt.getList("guess", NbtElement.LIST_TYPE);

        //Fill in the guesses from the user
        for (int i = 0; i < circleGuess.size() && i < this.circleGuess.size(); i++) {
            var ring = this.circleGuess.get(i);
            var ringNbt = circleGuess.getList(i);

            for (int j = 0; j < ringNbt.size() && i < ring.runeGuesses.size(); j++) {
                var guessString = ringNbt.getString(j);
                ring.setGuess(j, guessString.isEmpty() ? TheWorkRunes.NULL : new Identifier(guessString));
            }
        }

        checkIfCorrect();
    }

    public void cheatRecipe() {
        for (int i = 0; i < recipe.inputRings.length; i++) {
            var truth = recipe.inputRings[i];
            var guess = circleGuess.get(i);

            for (int j = 0; j < truth.length; j++) {
                guess.runeGuesses.set(j, truth[j].element().id);
            }
        }

        checkIfCorrect();
    }

    public class RingGuess {
        public final ArrayList<Identifier> runeGuesses = new ArrayList<>();

        public final float radius;

        public RingGuess(float radius) {
            this.radius = radius;
        }

        public void setGuess(int index, Identifier elementID) {
            //Can't change guesses once correct.
            if (isCorrect)
                return;

            runeGuesses.set(index, elementID);

            checkIfCorrect();
        }
    }
}
