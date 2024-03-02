package cassunshine.thework.data.recipes;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.ElementPacket;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Attempts to build deconstruction recipes out of any items that don't already have them specified in JSON.
 */
public class DeconstructionRecipeBuilder {

    private final HashMap<Item, EvaluatedItem> evaluatedItems = new HashMap<>();

    private HashMap<Item, ArrayList<GenericRecipe>> recipesForItem = new HashMap<>();
    private List<Item> allItems;

    private ArrayList<Item> rootItems = new ArrayList<>();
    private ArrayList<Item> nullItems = new ArrayList<>();


    private HashSet<GenericRecipe> checkingRecipes = new HashSet<>();

    private MinecraftServer server;

    private static final EvaluatedItem NULL = new EvaluatedItem(null);


    /**
     * Evaluates all recipes and attempts to build new deconstruction recipes based on their materials.
     */
    public void buildRecipes(MinecraftServer server, BiConsumer<Identifier, DeconstructionRecipe> recipeConsumer) {

        rootItems.clear();
        evaluatedItems.clear();
        checkingRecipes.clear();
        recipesForItem.clear();

        importExisting();
        tryBuild(server);

        allItems = null;

        try {
            var writer = new BufferedWriter(new FileWriter("root_items.json"));

            for (Item item : rootItems) {
                writer.write(Registries.ITEM.getId(item) + "\n");
            }

            writer.close();

            writer = new BufferedWriter(new FileWriter("null_items.json"));

            for (Item item : nullItems) {
                writer.write(Registries.ITEM.getId(item) + "\n");
            }

            writer.close();
        } catch (Exception e) {
            //Ignore
            TheWorkMod.LOGGER.error(e.toString());
        }
    }

    private void importExisting() {
        var definedRecipes = TheWorkRecipes.getAllDeconstruction();

        //For each recipe, evaluate the elements it has, and put it in as an already-evaluated recipe.
        for (DeconstructionRecipe recipe : definedRecipes) {
            var item = Registries.ITEM.get(recipe.id());
            var evaluated = new EvaluatedItem(item);

            for (ElementPacket packet : recipe.output()) {
                evaluated.result.put(packet.element(), packet.amount());
                evaluated.totalElement += packet.amount();
            }

            evaluatedItems.put(item, evaluated);
        }
    }

    private void tryBuild(MinecraftServer server) {
        this.server = server;
        recipesForItem = new HashMap<>();
        allItems = Registries.ITEM.stream().toList();

        var start = System.nanoTime();

        {
            collectRecipes(RecipeType.CRAFTING, (r) -> {
                var genRec = new GenericRecipe();
                genRec.input = r.getIngredients().toArray(new Ingredient[0]);
                genRec.output = r.getResult(server.getRegistryManager());
                return genRec;
            });

            collectRecipes(RecipeType.SMELTING, (r) -> {
                var genRec = new GenericRecipe();
                genRec.input = r.getIngredients().toArray(new Ingredient[0]);
                genRec.output = r.getResult(server.getRegistryManager());
                return genRec;
            });

            collectRecipes(RecipeType.SMOKING, (r) -> {
                var genRec = new GenericRecipe();
                genRec.input = r.getIngredients().toArray(new Ingredient[0]);
                genRec.output = r.getResult(server.getRegistryManager());
                return genRec;
            });

            collectRecipes(RecipeType.SMITHING, (r) -> {
                var genRec = new GenericRecipe();
                genRec.input = r.getIngredients().toArray(new Ingredient[0]);
                genRec.output = r.getResult(server.getRegistryManager());
                return genRec;
            });

            collectRecipes(RecipeType.BLASTING, (r) -> {
                var genRec = new GenericRecipe();
                genRec.input = r.getIngredients().toArray(new Ingredient[0]);
                genRec.output = r.getResult(server.getRegistryManager());
                return genRec;
            });

            collectRecipes(RecipeType.CAMPFIRE_COOKING, (r) -> {
                var genRec = new GenericRecipe();
                genRec.input = r.getIngredients().toArray(new Ingredient[0]);
                genRec.output = r.getResult(server.getRegistryManager());
                return genRec;
            });

            collectRecipes(RecipeType.STONECUTTING, (r) -> {
                var genRec = new GenericRecipe();
                genRec.input = r.getIngredients().toArray(new Ingredient[0]);
                genRec.output = r.getResult(server.getRegistryManager());
                return genRec;
            });
        }

        for (Block block : Registries.BLOCK) {
            evaluateItemCost(block.asItem());
        }

        for (Item item : allItems) {
            evaluateItemCost(item);
        }

        var end = System.nanoTime();

        var difference = end - start;

        TheWorkMod.LOGGER.info("Generated recipes in {}ms", (difference / 1_000_000));
    }

    private <T extends Recipe<C>, C extends Inventory> void collectRecipes(RecipeType<T> type, Function<T, GenericRecipe> recipeSupplier) {
        var allRecipes = server.getRecipeManager().listAllOfType(type);

        //Put all recipes for each item into a map so we can easily reference them.
        for (RecipeEntry<T> recipe : allRecipes) {
            var results = recipe.value().getResult(server.getRegistryManager());
            var item = results.getItem();

            var genRecipe = recipeSupplier.apply(recipe.value());

            if (genRecipe.input.length == 0 || genRecipe.output.isEmpty()) {
                continue;
                //TheWorkMod.LOGGER.error("Recipe has no input for item {}", item);
            }

            recipesForItem.computeIfAbsent(item, i -> new ArrayList<>()).add(genRecipe);
        }
    }

    private EvaluatedItem evaluateItemCost(Item item) {
        //If the item has already been evaluated, simply return that.
        if (evaluatedItems.containsKey(item))
            return evaluatedItems.get(item);

        //Get all the recipes that output this item.
        var recipeList = recipesForItem.get(item);

        //If there are no recipes for this item, this recipe branch is not valid.
        if (recipeList == null || recipeList.isEmpty()) {
            evaluatedItems.putIfAbsent(item, NULL);
            rootItems.add(item);
            return NULL;
        }

        //Find the recipe with the least output.
        GenericRecipe minCostRecipe = null;
        HashMap<Element, Integer> minCostResult = null;
        int minimumCost = Integer.MAX_VALUE;

        for (GenericRecipe recipe : recipeList) {

            //Don't allow recursive checks of the same recipe.
            if (checkingRecipes.contains(recipe))
                continue;

            //Add recipe so we can't check it again.
            checkingRecipes.add(recipe);

            HashMap<Element, Integer> recipeResult = new HashMap<>();
            int recipeCost = 0;

            //For each ingredient for this recipe...
            for (Ingredient ingredient : recipe.input) {
                var stacks = ingredient.getMatchingStacks();

                //Ignore stacks that are empty.
                if (stacks.length == 0)
                    continue;

                //Find the ingredient with the lowest output.
                ItemStack leastCostStack = null;
                EvaluatedItem leastCostEvaluated = null;
                for (ItemStack stack : stacks) {
                    var stackItem = stack.getItem();
                    var stackEval = evaluateItemCost(stackItem);

                    if (stackEval == NULL)
                        continue;

                    //If the new stack & evaluated item cost less than the last, take them.
                    if (leastCostEvaluated == null || (stackEval.totalElement * stack.getCount() < leastCostEvaluated.totalElement * leastCostStack.getCount())) {
                        leastCostStack = stack;
                        leastCostEvaluated = stackEval;
                    }
                }

                //If no ingredient was found, mark recipe invalid, break.
                if (leastCostEvaluated == null) {
                    recipeResult = null;
                    break;
                }

                recipeCost += leastCostEvaluated.totalElement * leastCostStack.getCount();

                //Put ingredient into recipe.
                for (Element element : leastCostEvaluated.result.keySet()) {
                    var existing = recipeResult.computeIfAbsent(element, (e) -> 0);
                    recipeResult.put(element, existing + (leastCostEvaluated.result.get(element) * leastCostStack.getCount()));
                }
            }

            //Done checking recipe, so, remove it.
            checkingRecipes.remove(recipe);

            //If there was an ingredient with no cost, this recipe is not valid for consideration.
            //We only consider recipes where all ingredients have valid elements.s
            if (recipeResult == null || recipeCost > minimumCost)
                continue;

            //Record this as lowers.
            minimumCost = recipeCost;
            minCostRecipe = recipe;
            minCostResult = recipeResult;
        }

        //If there was no recipe that was valid, just return null.
        if (minCostRecipe == null || minCostResult.isEmpty()) {
            evaluatedItems.putIfAbsent(item, NULL);
            nullItems.add(item);
            return NULL;
        }


        var evaluated = new EvaluatedItem(item);
        var count = minCostRecipe.output.getCount();

        for (Element element : minCostResult.keySet()) {
            var amount = minCostResult.get(element);
            amount = MathHelper.floor(amount / (float) count);

            if (amount <= 0)
                continue;

            evaluated.result.put(element, amount);
            evaluated.totalElement += amount;
        }

        if (evaluated.totalElement == 0) {
            evaluatedItems.put(item, NULL);
            nullItems.add(item);
            return NULL;
        }

        evaluatedItems.put(item, evaluated);
        return evaluated;
    }


    private static class GenericRecipe {
        public Ingredient[] input;
        public ItemStack output;

        public ItemStack remainder;
    }

    private static class EvaluatedItem {
        public final Item item;

        public final HashMap<Element, Integer> result = new HashMap<>();
        public int totalElement = 0;

        private EvaluatedItem(Item item) {
            this.item = item;
        }
    }
}
