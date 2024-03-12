package cassunshine.thework.data.recipes;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.chemistry.ChemistryWorkType;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.ElementPacket;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.alchemy.elements.inventory.ElementInventory;
import cassunshine.thework.utils.ShiftSorting;
import cassunshine.thework.utils.TheWorkUtils;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.*;

public class TheWorkRecipes {

    private static final Gson gson = new Gson();

    private static ImmutableMap<Identifier, DeconstructionRecipe> DECONSTRUCTION_RECIPES = ImmutableMap.of();
    private static ImmutableMap<String, ConstructionRecipe> CONSTRUCTION_RECIPES = ImmutableMap.of();
    private static ImmutableMap<Item, ConstructionRecipe> CONSTRUCTION_RECIPES_BY_ITEM = ImmutableMap.of();

    private static ArrayList<ReactionRecipe> REACTIONS = new ArrayList<>();


    public static ImmutableCollection<DeconstructionRecipe> getAllDeconstruction() {
        return DECONSTRUCTION_RECIPES.values();
    }

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

    public static void loadRecipes(MinecraftServer server, ResourceManager resourceManager) {
        loadDeconstructionRecipes(server, resourceManager);
        loadConstructionRecipes(resourceManager);
        loadReactionRecipes(resourceManager);
    }

    private static void loadDeconstructionRecipes(MinecraftServer server, ResourceManager resourceManager) {
        Map<Identifier, Resource> recipes = resourceManager.findResources("alchemy/deconstruction", p -> p.getPath().endsWith(".json"));
        ImmutableMap.Builder<Identifier, DeconstructionRecipe> builder = ImmutableMap.builder();

        for (var entry : recipes.entrySet()) {
            var value = entry.getValue();

            try {
                var json = gson.fromJson(value.getReader(), JsonObject.class);

                var inputID = new Identifier(json.get("item").getAsString());
                var outputJson = json.get("elements").getAsJsonObject();

                int totalElement = 0;
                ElementPacket[] outputsList = new ElementPacket[outputJson.size()];
                int index = 0;

                for (Map.Entry<String, JsonElement> jsonEntry : outputJson.entrySet()) {
                    Element element = Elements.getElement(new Identifier(TheWorkMod.ModID, jsonEntry.getKey()));
                    if (element == null) throw new Exception("Element " + jsonEntry.getKey() + "not found");

                    int amount = jsonEntry.getValue().getAsInt();

                    outputsList[index++] = (new ElementPacket(element, amount));
                    totalElement += amount;
                }

                builder.put(inputID, new DeconstructionRecipe(inputID, totalElement + 1, outputsList));
            } catch (Exception e) {
                TheWorkMod.LOGGER.error(e.toString());
            }
        }

        //Build recipes so that we can use already-loaded recipes.
        DECONSTRUCTION_RECIPES = builder.build();

        //Build any recipes we don't have manually defined automatically.
        new DeconstructionRecipeBuilder().buildRecipes(server, (i, r) -> {
            if (!DECONSTRUCTION_RECIPES.containsKey(i))
                builder.put(i, r);
        });
        DECONSTRUCTION_RECIPES = builder.build();

        TheWorkMod.LOGGER.info("Total deconstruction recipe count: {}", DECONSTRUCTION_RECIPES.size());
    }

    private static void loadConstructionRecipes(ResourceManager resourceManager) {
        Map<Identifier, Resource> recipes = resourceManager.findResources("alchemy/construction", p -> p.getPath().endsWith(".json"));
        ImmutableMap.Builder<String, ConstructionRecipe> constructRecipes = ImmutableMap.builder();
        ImmutableMap.Builder<Item, ConstructionRecipe> constructRecipesByItem = ImmutableMap.builder();

        HashSet<String> _usedSignatures = new HashSet<>();

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

                            if (element == Elements.NONE)
                                throw new Exception("Element not found!!");

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
                if (_usedSignatures.contains(recipe.signature)) {
                    var f = constructRecipes.build();

                    TheWorkMod.LOGGER.error("Signature for recipe " + recipe.signature + " already exists, and is taken by item " + f.get(recipe.signature).outputs[0].getItem());
                    continue;
                }

                constructRecipes.put(recipe.signature, recipe);
                _usedSignatures.add(recipe.signature);

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
                        var val = inputJson.get(key).getAsInt();
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
                        var val = outputJson.get(key).getAsInt();
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

    public static void writeSync(PacketByteBuf buf) {

        //Deconstruction
        {
            buf.writeInt(DECONSTRUCTION_RECIPES.size());

            for (var entry : DECONSTRUCTION_RECIPES.entrySet()) {
                buf.writeIdentifier(entry.getKey());

                var value = entry.getValue();
                var output = value.output();

                buf.writeInt(value.time());

                buf.writeInt(output.length);
                for (ElementPacket elementPacket : output) {
                    buf.writeIdentifier(elementPacket.element().id);
                    buf.writeInt(elementPacket.amount());
                }
            }
        }

        //Construction
        {
            buf.writeInt(CONSTRUCTION_RECIPES.size());

            //NOTE - we don't need to write the key for construction recipes, the key is just their signature, which the client should calculate anyway.
            for (var entry : CONSTRUCTION_RECIPES.entrySet()) {

                var value = entry.getValue();

                //Outputs
                buf.writeInt(value.outputs.length);
                for (ItemStack output : value.outputs)
                    buf.writeItemStack(output);

                //Inputs
                buf.writeInt(value.inputRings.length);
                for (var ring : value.inputRings) {
                    buf.writeInt(ring.length);
                    for (ElementPacket elementPacket : ring) {
                        buf.writeIdentifier(elementPacket.element().id);
                        buf.writeInt(elementPacket.amount());
                    }
                }
            }
        }
    }

    public static void readSync(PacketByteBuf buf) {
        //Deconstruction
        {
            ImmutableMap.Builder<Identifier, DeconstructionRecipe> builder = new ImmutableMap.Builder<>();
            int numEntries = buf.readInt();
            for (int i = 0; i < numEntries; i++) {
                var id = buf.readIdentifier();

                var time = buf.readInt();
                var output = new ElementPacket[buf.readInt()];
                for (int j = 0; j < output.length; j++) {
                    output[j] = new ElementPacket(Elements.getElement(buf.readIdentifier()), buf.readInt());
                }

                var recipe = new DeconstructionRecipe(id, time, output);
                builder.put(id, recipe);
            }

            DECONSTRUCTION_RECIPES = builder.build();
        }

        //Construction
        {
            ImmutableMap.Builder<String, ConstructionRecipe> builder = new ImmutableMap.Builder<>();
            int numEntries = buf.readInt();
            for (int i = 0; i < numEntries; i++) {
                var inputs = new ItemStack[buf.readInt()];
                for (int j = 0; j < inputs.length; j++) {
                    inputs[j] = buf.readItemStack();
                }

                var outputs = new ElementPacket[buf.readInt()][];
                for (int j = 0; j < outputs.length; j++) {
                    var ring = new ElementPacket[buf.readInt()];
                    for (int k = 0; k < ring.length; k++) {
                        ring[k] = new ElementPacket(Elements.getElement(buf.readIdentifier()), buf.readInt());
                    }

                    outputs[j] = ring;
                }

                var recipe = new ConstructionRecipe(outputs, inputs, TheWorkUtils.generateSignature(outputs, r -> TheWorkUtils.generateSignature(r, e -> e.element().id.toString())));
                builder.put(recipe.signature, recipe);
            }

            CONSTRUCTION_RECIPES = builder.build();
        }
    }
}