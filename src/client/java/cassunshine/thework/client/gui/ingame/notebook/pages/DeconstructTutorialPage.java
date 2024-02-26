package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.client.gui.ingame.notebook.drawables.ItemDisplay;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class DeconstructTutorialPage extends AlchemistNotebookPage {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "deconstruct_tutorial");
    public static final Identifier PAGE_TEXTURE = new Identifier(TheWorkMod.ModID, "textures/pages/deconstruct_tutorial.png");

    public DeconstructTutorialPage(NbtCompound compound) {
        super(compound);
    }

    public DeconstructTutorialPage() {
        super(IDENTIFIER, PAGE_TEXTURE);
    }

    @Override
    public void init(AlchemistNotebookScreen screen, int x, int y, int width, int height) {
        super.init(screen, x, y, width, height);

        var itemDisplay = new ItemDisplay();
        itemDisplay.x = x + MathHelper.floor(width * (14.5f / 96.0f));
        itemDisplay.y = y + MathHelper.floor(height * (38.5f / 128.0f));
        itemDisplay.stack = new ItemStack(Blocks.DIRT.asItem());

        screen.addDrawable(itemDisplay);

        itemDisplay = new ItemDisplay();
        itemDisplay.x = x + MathHelper.floor(width * (14.5f / 96.0f));
        itemDisplay.y = y + MathHelper.floor(height * (54.5f / 128.0f));
        itemDisplay.stack = new ItemStack(Blocks.STONE.asItem());

        screen.addDrawable(itemDisplay);
    }
}