package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.elements.ElementPacket;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.items.notebook.AlchemistNotebookItem;
import cassunshine.thework.recipes.ConstructionRecipe;
import cassunshine.thework.recipes.TheWorkRecipes;
import cassunshine.thework.rendering.alchemy.AlchemyCircleRenderer;
import cassunshine.thework.rendering.util.RenderingUtilities;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;

/**
 * This page holds the recipe for a single item, and the guesses the player has made for its recipe.
 * Players automatically know how many rings the recipe will take.
 */
public class RecipePage extends AlchemistNotebookPage {

    private final ConstructionRecipe recipe;

    public final ArrayList<RingGuess> ringGuesses = new ArrayList<>();

    private final Identifier itemID;

    private final ItemStack recipeOutputStack;

    public boolean isCorrect;


    /**
     * Constructs the page using an item's ID and the NBT data (if any) for the guesses on this page.
     */
    public RecipePage(Identifier itemID, NbtList nbt) {
        this.itemID = itemID;
        var item = Registries.ITEM.get(itemID);
        recipe = TheWorkRecipes.getConstruction(item);

        if (recipe == null)
            throw new RuntimeException("Unable to find recipe for item " + itemID.toString());

        float minRadius = 1.5f;

        //Fill out the ring guesses based on the recipe
        for (ElementPacket[] ring : recipe.inputRings) {
            var ringGuess = new RingGuess(minRadius++);
            for (ElementPacket elementPacket : ring)
                ringGuess.runeGuesses.add(TheWorkRunes.NULL);

            ringGuesses.add(ringGuess);
        }

        //Fill in the guesses from the user
        for (int i = 0; i < nbt.size() && i < ringGuesses.size(); i++) {
            var ring = ringGuesses.get(i);
            var ringNbt = nbt.getList(i);

            for (int j = 0; j < ringNbt.size() && i < ring.runeGuesses.size(); j++) {
                var guessString = ringNbt.getString(j);
                ring.setGuess(j, guessString.isEmpty() ? TheWorkRunes.NULL : new Identifier(guessString));
            }
        }

        checkIfCorrect();
        recipeOutputStack = new ItemStack(item);
    }

    public void checkIfCorrect() {
        for (int i = 0; i < recipe.inputRings.length; i++) {
            var ring = ringGuesses.get(i);
            var ringRecipe = recipe.inputRings[i];

            for (int j = 0; j < ringRecipe.length; j++) {
                if (!ring.runeGuesses.get(j).equals(ringRecipe[j].element().id)) {
                    isCorrect = false;
                    return;
                }
            }
        }

        isCorrect = true;
    }

    @Override
    public void addInteractables(AlchemistNotebookScreen screen, int x, int y) {
        super.addInteractables(screen, x, y);

        if (isCorrect)
            return;

        int baseX = x + AlchemistNotebookPage.PAGE_WIDTH / 2;
        int baseY = y + AlchemistNotebookPage.PAGE_HEIGHT / 2;

        for (RingGuess ringGuess : ringGuesses) {
            var runes = ringGuess.runeGuesses;

            for (int i = 0; i < runes.size(); i++) {
                float angle = ((float) i / runes.size()) * MathHelper.TAU;

                var centerPos = new Vec3d(baseX + (MathHelper.sin(MathHelper.TAU - angle) * ringGuess.radius) * 16, baseY + (-MathHelper.cos(MathHelper.TAU - angle) * ringGuess.radius) * 16, 0);

                int finalI = i;
                ButtonWidget widget = new TexturedButtonWidget(MathHelper.floor(centerPos.x - 7), MathHelper.floor(centerPos.y - 7), 14, 15, new ButtonTextures(new Identifier("recipe_book/page_forward"), new Identifier("recipe_book/page_forward_highlighted")),
                        b -> {
                            if (isCorrect)
                                return;
                            var cRune = runes.get(finalI);
                            var nextID = (Elements.getElement(cRune).number + 1) % Elements.getElementCount();
                            var nextRune = Elements.getElement(nextID);
                            runes.set(finalI, nextRune.id);

                            checkIfCorrect();

                            AlchemistNotebookItem.putRecipe(screen.stack.getOrCreateNbt(), itemID, writeNbt());
                            screen.syncNbt();
                        },
                        Text.empty()
                );

                screen.addSelectableChild(widget);
            }
        }
    }

    @Override
    public void render() {
        RenderingUtilities.setupNormal(0, 0, 0);

        RenderingUtilities.pushMat();

        //Render label
        {
            RenderingUtilities.pushMat();
            RenderingUtilities.setupColor(0xFF000000);
            ;
            RenderingUtilities.translateMatrix(PAGE_WIDTH / 2.0f, 10, 0);
            RenderingUtilities.drawTextCentered(recipeOutputStack.getItem().getName(), false);
            RenderingUtilities.popMat();
        }

        RenderingUtilities.translateMatrix(PAGE_WIDTH / 2.0f, PAGE_HEIGHT / 2.0f, 0);

        //Render item.
        {
            RenderingUtilities.pushMat();
            //if (RenderingUtilities.SPACE == RenderingUtilities.RenderingSpace.GUI)
            RenderingUtilities.getMatStack().multiplyPositionMatrix(new Matrix4f().scale(1, -1, 1));
            RenderingUtilities.scaleMatrix(16.0f, 16.0f, 16.0f);
            RenderingUtilities.translateMatrix(0, 0, 0.21f);

            RenderingUtilities.renderItem(recipeOutputStack, ModelTransformationMode.GUI, null);

            RenderingUtilities.popMat();
        }

        RenderingUtilities.scaleMatrix(16.0f, -16.0f, 16.0f);
        RenderingUtilities.rotateMatrix(MathHelper.HALF_PI, 0, 0);

        for (RingGuess ringGuess : ringGuesses) {
            var runes = ringGuess.runeGuesses;
            float circumference = ringGuess.radius * MathHelper.TAU;
            float widthNode = (0.5f / circumference) * MathHelper.TAU;

            int unitsPerSegment = MathHelper.ceil((circumference / runes.size()) * 1.5f);

            for (int i = 0; i < runes.size(); i++) {
                var element = Elements.getElement(runes.get(i));
                float angle = ((float) i / runes.size()) * MathHelper.TAU;
                float angleNext = ((float) (i + 1) / runes.size()) * MathHelper.TAU;

                RenderingUtilities.setupColor(0xFFFFFFFF);

                RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutoutNoCull(new Identifier(TheWorkMod.ModID, "textures/other/alchemy_circle.png")));
                AlchemyCircleRenderer.drawCircleSegment(ringGuess.radius, angle + widthNode, angleNext - widthNode, unitsPerSegment);

                RenderingUtilities.pushMat();
                RenderingUtilities.rotateMatrix(0, angle, 0);
                RenderingUtilities.translateMatrix(0, 0, -ringGuess.radius);

                if (element != null)
                    RenderingUtilities.setupColor(element.color);

                AlchemyCircleRenderer.drawSidedCircleAndRune(0.5f, 6, element == Elements.NONE ? TheWorkRunes.NULL : element.id, -angle);

                RenderingUtilities.popMat();
            }
        }

        RenderingUtilities.popMat();
    }

    public NbtList writeNbt() {
        var ringList = new NbtList();

        for (RingGuess guess : ringGuesses) {
            var runeList = new NbtList();

            for (Identifier runeGuess : guess.runeGuesses) {
                runeList.add(NbtString.of(runeGuess.toString()));
            }

            ringList.add(runeList);
        }

        return ringList;
    }

    private class RingGuess {
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
