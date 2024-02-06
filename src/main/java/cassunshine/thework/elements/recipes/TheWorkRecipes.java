package cassunshine.thework.elements.recipes;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.elements.Element;
import cassunshine.thework.elements.ElementPacket;
import cassunshine.thework.elements.TheWorkElements;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Map;

public class TheWorkRecipes {

    private static final Gson gson = new Gson();

    private static ImmutableMap<Identifier, DeconstructionRecipe> DECONSTRUCTION_RECIPES = ImmutableMap.of();

    /**
     * Returns the recipe to deconstruct the given ID, or null if none exists.
     */
    public static DeconstructionRecipe getDeconstruction(Identifier recipeID) {
        return DECONSTRUCTION_RECIPES.get(recipeID);
    }

    public static void loadRecipes(ResourceManager resourceManager) {
        loadDeconstructionRecipes(resourceManager);
    }


    private static void loadDeconstructionRecipes(ResourceManager resourceManager) {
        Map<Identifier, Resource> recipes = resourceManager.findResources("recipes/deconstruction", p -> p.getPath().endsWith(".json"));
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
                    Element element = TheWorkElements.getElement(new Identifier(TheWorkMod.ModID, jsonEntry.getKey()));
                    if (element == null) throw new Exception("Element " + jsonEntry.getKey() + "not found");

                    int amount = jsonEntry.getValue().getAsInt();

                    outputsList[index++] = (new ElementPacket(element, amount));
                }

                builder.put(inputID, new DeconstructionRecipe(inputID, outputsList));
            } catch (Exception e) {
                TheWorkMod.LOGGER.error(e.toString());
            }
        }

        DECONSTRUCTION_RECIPES = builder.build();
    }
}
