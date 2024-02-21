package cassunshine.thework.client.gui.ingame.notebook;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class AlchemistNotebookNodeScreen extends AlchemistNotebookScreen {
    public AlchemistNotebookNodeScreen() {
        super(Text.translatable("ui.alchemist_notebook.page.node.title"), "nodes");


    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
