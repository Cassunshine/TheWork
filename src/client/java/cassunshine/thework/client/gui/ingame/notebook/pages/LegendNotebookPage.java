package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.client.gui.ingame.notebook.drawables.RandomItemDisplay;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class LegendNotebookPage extends AlchemistNotebookPage {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "legend");
    public static final Identifier PAGE_TEXTURE = new Identifier(TheWorkMod.ModID, "textures/pages/legend.png");

    public LegendNotebookPage(NbtCompound compound) {
        super(compound);
    }

    public LegendNotebookPage() {
        super(IDENTIFIER, PAGE_TEXTURE);
    }

    @Override
    public void init(AlchemistNotebookScreen screen, int x, int y, int width, int height) {
        super.init(screen, x, y, width, height);

        var randomItemDisplay = new RandomItemDisplay();
        randomItemDisplay.x = x + MathHelper.floor(width * 0.61);
        randomItemDisplay.y = y + MathHelper.floor(height * 0.61);

        screen.addDrawable(randomItemDisplay);
    }
}
