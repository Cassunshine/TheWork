package cassunshine.thework.recipes;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.chemistry.ChemistryWorkType;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.ElementPacket;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.alchemy.elements.inventory.ElementInventory;
import cassunshine.thework.utils.ShiftSorting;
import cassunshine.thework.utils.TheWorkUtils;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class TheWorkRecipes {

    private static final Gson gson = new Gson();

    private static ImmutableMap<Identifier, DeconstructionRecipe> DECONSTRUCTION_RECIPES = ImmutableMap.of();
    private static ImmutableMap<String, ConstructionRecipe> CONSTRUCTION_RECIPES = ImmutableMap.of();
    private static ImmutableMap<Item, ConstructionRecipe> CONSTRUCTION_RECIPES_BY_ITEM = ImmutableMap.of();

    private static ArrayList<ReactionRecipe> REACTIONS = new ArrayList<>();


    /**
     * Returns the recipe to deconstruct the given ID, or null if none exists.
     */
    public static DeconstructionRecipe getDeconstruction(Identifier recipeID) {
        return DECONSTRUCTION_RECIPES.get(recipeID);
    }

    public static ConstructionRecipe getConstruction(String signature) {
        return CONSTRUCTION_RECIPES.get(signature);
    }

    public static ConstructionRecipe getConstruction(Item item) {
        if (item == null)
            return null;

        return CONSTRUCTION_RECIPES_BY_ITEM.get(item);
    }

    /**
     * Finds all matching reactions, putting them into a list.
     */
    public static void findReactions(float temperature, ElementInventory inputs, ChemistryWorkType workType, ArrayList<ReactionRecipe> target) {

        for (ReactionRecipe reaction : REACTIONS) {
            //If the work type is wrong
            if (workType != reaction.requiredWork) continue;
            //If temperature is outside of range
            if (temperature > reaction.maxTemperature || temperature < reaction.minTemperature) continue;
            //If the inputs don't match up.
            boolean hasRightInputs = true;
            for (var reactionInput : reaction.inputs) {
                if (!inputs.has(reactionInput.element(), reactionInput.amount())) {
                    hasRightInputs = false;
                    break;
                }
            }
            if (!hasRightInputs) continue;

            target.add(reaction);
        }
    }

    public static void loadRecipes(ResourceManager resourceManager) {
        loadDeconstructionRecipes(resourceManager);
        loadConstructionRecipes(resourceManager);
        loadReactionRecipes(resourceManager);
    }

    private static void loadDeconstructionRecipes(ResourceManager resourceManager) {
        Map<Identifier, Resource> recipes = resourceManager.findResources("alchemy/deconstruction", p -> p.getPath().endsWith(".json"));
        ImmutableMap.Builder<Identifier, DeconstructionRecipe> builder = ImmutableMap.builder();

        for (var entry : recipes.entrySet()) {
            var value = entry.getValue();

            try {
                var json = gson.fromJson(value.getReader(), JsonObject.class);

                var inputID = new Identifier(json.get("input").getAsString());
                var outputJson = json.get("output").getAsJsonObject();

                ElementPacket[] outputsList = new ElementPacket[outputJson.size()];
                int index = 0;

                for (Map.Entry<String, JsonElement> jsonEntry : outputJson.entrySet()) {
                    Element element = Elements.getElement(new Identifier(TheWorkMod.ModID, jsonEntry.getKey()));
                    if (element == null) throw new Exception("Element " + jsonEntry.getKey() + "not found");

                    float amount = jsonEntry.getValue().getAsFloat();

                    outputsList[index++] = (new ElementPacket(element, amount));
                }

                int time = json.has("time") ? json.get("time").getAsInt() : 10;

                builder.put(inputID, new DeconstructionRecipe(inputID, time, outputsList));
            } catch (Exception e) {
                TheWorkMod.LOGGER.error(e.toString());
            }
        }

        DECONSTRUCTION_RECIPES = builder.build();
    }

    private static void loadConstructionRecipes(ResourceManager resourceManager) {
        Map<Identifier, Resource> recipes = resourceManager.findResources("alchemy/construction", p -> p.getPath().endsWith(".json"));
        ImmutableMap.Builder<String, ConstructionRecipe> constructRecipes = ImmutableMap.builder();
        ImmutableMap.Builder<Item, ConstructionRecipe> constructRecipesByItem = ImmutableMap.builder();

        for (var entry : recipes.entrySet()) {
            var value = entry.getValue();

            try {
                var json = gson.fromJson(value.getReader(), JsonObject.class);

                ElementPacket[][] rings;
                ItemStack[] outputs;

                //Inputs...
                {
                    var jsonInputList = json.get("inputs").getAsJsonArray();
                    rings = new ElementPacket[jsonInputList.size()][];

                    for (int i = 0; i < jsonInputList.size(); i++) {
                        var jsonInputRing = jsonInputList.get(i).getAsJsonArray();

                        var ring = new ElementPacket[jsonInputRing.size()];
                        for (int j = 0; j < jsonInputRing.size(); j++) {
                            var jsonRingEntry = jsonInputRing.get(j).getAsJsonObject();
                            var element = Elements.getElement(new Identifier(TheWorkMod.ModID, jsonRingEntry.get("element").getAsString()));

                            if (element == Elements.NONE) throw new Exception("Element not found!!");

                            ring[j] = new ElementPacket(element, jsonRingEntry.get("amount").getAsInt());
                        }

                        var offset = ShiftSorting.findShiftValue(ring, r -> r.element().number);
                        ShiftSorting.rotateArray(ring, offset);

                        rings[i] = ring;
                    }
                }

                //Outputs...
                {
                    var jsonOutputList = json.get("outputs").getAsJsonObject();
                    outputs = new ItemStack[jsonOutputList.size()];

                    int index = 0;
                    for (var outputEntry : jsonOutputList.entrySet()) {
                        var stack = new ItemStack(Registries.ITEM.get(new Identifier(outputEntry.getKey())));
                        stack.setCount(outputEntry.getValue().getAsInt());
                        outputs[index++] = stack;
                    }
                }

                var recipe = new ConstructionRecipe(rings, outputs, TheWorkUtils.generateSignature(rings, r -> TheWorkUtils.generateSignature(r, e -> e.element().id.toString())));
                constructRecipes.put(recipe.signature, recipe);

                if (recipe.outputs.length == 1)
                    constructRecipesByItem.put(recipe.outputs[0].getItem(), recipe);
            } catch (Exception e) {
                TheWorkMod.LOGGER.error(e.toString());
            }
        }

        CONSTRUCTION_RECIPES = constructRecipes.build();
        CONSTRUCTION_RECIPES_BY_ITEM = constructRecipesByItem.build();
    }

    private static void loadReactionRecipes(ResourceManager resourceManager) {
        Map<Identifier, Resource> recipes = resourceManager.findResources("alchemy/reactions", p -> p.getPath().endsWith(".json"));
        REACTIONS.clear();

        for (var entry : recipes.entrySet()) {
            var value = entry.getValue();

            try {
                var json = gson.fromJson(value.getReader(), JsonObject.class);

                var recipe = new ReactionRecipe();

                recipe.minTemperature = json.has("min_temperature") ? json.get("min_temperature").getAsFloat() : Float.NEGATIVE_INFINITY;
                recipe.maxTemperature = json.has("max_temperature") ? json.get("max_temperature").getAsFloat() : Float.NEGATIVE_INFINITY;

                recipe.heatOutput = json.has("heat_output") ? json.get("heat_output").getAsFloat() : 0;
                recipe.order = json.has("order") ? json.get("order").getAsInt() : 0;

                recipe.requiredWork = json.has("required_work") ? ChemistryWorkType.valueOf(json.get("required_work").getAsString().toUpperCase(Locale.ROOT)) : ChemistryWorkType.NONE;

                //Read inputs
                if (json.has("inputs")) {
                    var inputJson = json.get("inputs").getAsJsonObject();
                    var inputPackets = new ElementPacket[inputJson.size()];

                    var index = 0;
                    for (var key : inputJson.keySet()) {
                        var val = inputJson.get(key).getAsFloat();
                        inputPackets[index++] = new ElementPacket(Elements.getElement(new Identifier(TheWorkMod.ModID, key)), val);
                    }

                    recipe.inputs = inputPackets;
                } else {
                    recipe.inputs = new ElementPacket[0];
                }

                //Read outputs
                if (json.has("outputs")) {
                    var outputJson = json.get("outputs").getAsJsonObject();
                    var outputPackets = new ElementPacket[outputJson.size()];

                    var index = 0;
                    for (var key : outputJson.keySet()) {
                        var val = outputJson.get(key).getAsFloat();
                        outputPackets[index++] = new ElementPacket(Elements.getElement(new Identifier(TheWorkMod.ModID, key)), val);
                    }

                    recipe.outputs = outputPackets;
                } else {
                    recipe.outputs = new ElementPacket[0];
                }

                REACTIONS.add(recipe);
            } catch (Exception e) {
                TheWorkMod.LOGGER.error(e.toString());
            }
        }
    }
}
