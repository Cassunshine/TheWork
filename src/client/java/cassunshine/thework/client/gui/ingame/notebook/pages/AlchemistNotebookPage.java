package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.rendering.util.RenderingUtilities;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class AlchemistNotebookPage {

    public static final int PAGE_SCALE = 3;
    public static final int PAGE_WIDTH = 48 * PAGE_SCALE;
    public static final int PAGE_HEIGHT = 64 * PAGE_SCALE;

    /**
     * Adds any interactable UI elements to the UI screen.
     */
    public void addInteractables(AlchemistNotebookScreen screen, int x, int y) {

    }

    /**
     * Draws everything that's a part of this notebook page.
     */
    public void render() {

    }


    public static void generateRecipePages(ArrayList<AlchemistNotebookPage> pageList, NbtCompound nbt) {
        var recipeList = nbt.getList("recipeList", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < recipeList.size(); i++) {
            var recipeCompound = recipeList.getCompound(i);

            var id = new Identifier(recipeCompound.getString("id"));
            var guess = recipeCompound.getList("guess", NbtElement.LIST_TYPE);

            pageList.add(new RecipePage(id, guess));
        }
    }
}
