package cassunshine.thework.elements.recipes;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.elements.Element;
import cassunshine.thework.elements.ElementPacket;
import cassunshine.thework.elements.Elements;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Map;

public class TheWorkRecipes {

    private static final Gson gson = new Gson();

    private static ImmutableMap<Identifier, DeconstructionRecipe> DECONSTRUCTION_RECIPES = ImmutableMap.of();
    private static ImmutableMap<String, ConstructionRecipe> CONSTRUCTION_RECIPES = ImmutableMap.of();


    /**
     * Returns the recipe to deconstruct the given ID, or null if none exists.
     */
    public static DeconstructionRecipe getDeconstruction(Identifier recipeID) {
        return DECONSTRUCTION_RECIPES.get(recipeID);
    }

    public static ConstructionRecipe getConstruction(String signature) {
        return CONSTRUCTION_RECIPES.get(signature);
    }

    public static void loadRecipes(ResourceManager resourceManager) {
        loadDeconstructionRecipes(resourceManager);
        loadConstructionRecipes(resourceManager);
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

                float time = json.has("time") ? json.get("time").getAsInt() : 10;

                builder.put(inputID, new DeconstructionRecipe(inputID, time, outputsList));
            } catch (Exception e) {
                TheWorkMod.LOGGER.error(e.toString());
            }
        }

        DECONSTRUCTION_RECIPES = builder.build();
    }

    private static void loadConstructionRecipes(ResourceManager resourceManager) {
        Map<Identifier, Resource> recipes = resourceManager.findResources("alchemy/construction", p -> p.getPath().endsWith(".json"));
        ImmutableMap.Builder<String, ConstructionRecipe> builder = ImmutableMap.builder();

        for (var entry : recipes.entrySet()) {
            var value = entry.getValue();

            try {
                var json = gson.fromJson(value.getReader(), JsonObject.class);

                ConstructionRecipe.Ring[] rings;
                Object2IntMap<Identifier> outputs;

                //Inputs...
                {
                    var jsonInputList = json.get("inputs").getAsJsonArray();
                    rings = new ConstructionRecipe.Ring[jsonInputList.size()];

                    for (int i = 0; i < jsonInputList.size(); i++) {
                        var jsonInputRing = jsonInputList.get(i).getAsJsonArray();

                        ConstructionRecipe.Ring.Entry[] entries = new ConstructionRecipe.Ring.Entry[jsonInputRing.size()];
                        for (int j = 0; j < jsonInputRing.size(); j++) {
                            var jsonRingEntry = jsonInputRing.get(i).getAsJsonObject();
                            var element = Elements.getElement(new Identifier(TheWorkMod.ModID, jsonRingEntry.get("element").getAsString()));

                            if (element == Elements.NONE)
                                throw new Exception("Element not found!!");

                            entries[j] = new ConstructionRecipe.Ring.Entry(element, jsonRingEntry.get("amount").getAsFloat());
                        }

                        rings[i] = new ConstructionRecipe.Ring(entries);
                    }
                }

                //Outputs...
                {
                    var jsonOutputList = json.get("outputs").getAsJsonObject();
                    outputs = new Object2IntOpenHashMap<>();

                    for (var outputEntry : jsonOutputList.entrySet())
                        outputs.put(new Identifier(outputEntry.getKey()), outputEntry.getValue().getAsInt());
                }

                var recipe = new ConstructionRecipe(rings, outputs);

                builder.put(ConstructionRecipe.buildSignature(recipe), recipe);
            } catch (Exception e) {
                TheWorkMod.LOGGER.error(e.toString());
            }
        }

        CONSTRUCTION_RECIPES = builder.build();
    }
}
