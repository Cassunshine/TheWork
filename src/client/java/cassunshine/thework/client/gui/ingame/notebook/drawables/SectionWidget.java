package cassunshine.thework.client.gui.ingame.notebook.drawables;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.items.notebook.sections.AlchemistNotebookSection;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SectionWidget extends TexturedButtonWidget {
    public static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(new Identifier(TheWorkMod.ModID, "notebook_tab"), new Identifier(TheWorkMod.ModID, "notebook_tab"));

    public final AlchemistNotebookScreen screen;
    public final AlchemistNotebookSection target;

    public SectionWidget(AlchemistNotebookScreen screen, AlchemistNotebookSection target, int width, int height) {
        super(width, height, BUTTON_TEXTURES, null, Text.empty());

        this.screen = screen;
        this.target = target;
    }

    @Override
    public void onPress() {
        screen.setCurrentSection(target);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (screen.currentSection != target) {
            context.setShaderColor(0.6f, 0.6f, 0.6f, 1f);
            context.drawItem(target.tabIcon, this.getX() + 4, this.getY() + 4);
            super.renderWidget(context, mouseX, mouseY, delta);
            context.setShaderColor(1f, 1.0f, 1f, 1f);
        } else {
            context.drawItem(target.tabIcon, this.getX() + 4, this.getY() + 4);
            super.renderWidget(context, mouseX, mouseY, delta);
        }
    }
}
