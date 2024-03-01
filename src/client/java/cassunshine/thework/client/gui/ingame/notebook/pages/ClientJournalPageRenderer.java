package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.assets.JournalLayout;
import cassunshine.thework.assets.elements.IconElement;
import cassunshine.thework.assets.elements.JournalLayoutElement;
import cassunshine.thework.assets.elements.TextElement;
import cassunshine.thework.client.rendering.util.RenderingUtilities;
import cassunshine.thework.client.utils.ClientJournalPage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.joml.Matrix4f;

public class ClientJournalPageRenderer extends AlchemistNotebookPageRenderer<ClientJournalPage> {

    public JournalLayout.JournalLayoutPage pageLayout;

    @Override
    public void init(ClientJournalPage target) {
        super.init(target);

        pageLayout = target.layout;
    }

    @Override
    public void render() {
        super.render();

        for (JournalLayoutElement element : pageLayout.elements) {
            RenderingUtilities.pushMat();

            RenderingUtilities.translateMatrix(element.x, element.y, 0);

            if (element instanceof TextElement text) {
                renderText(text, element.width, element.height);
            } else if (element instanceof IconElement icon) {
                renderIcon(icon, element.width, element.height);
            }
            RenderingUtilities.popMat();
        }
    }

    private void renderText(TextElement element, int width, int height) {
        RenderingUtilities.setupColor(0xFF000000);

        var textRenderer = MinecraftClient.getInstance().textRenderer;
        for (int i = 0; i < element.text.size(); i++) {
            var line = element.text.get(i);
            RenderingUtilities.drawText(line, false);
            RenderingUtilities.translateMatrix(0, textRenderer.fontHeight, 0);
        }

    }

    private void renderIcon(IconElement element, int width, int height) {
        RenderingUtilities.resetLightOverlay();
        RenderingUtilities.getMatStack().multiplyPositionMatrix(new Matrix4f().scale(1, -1, 1));
        RenderingUtilities.scaleMatrix(width, height, width);
        RenderingUtilities.renderItem(element.iconStack, ModelTransformationMode.GUI, null);
    }
}
