package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.assets.JournalLayout;
import cassunshine.thework.assets.elements.IconElement;
import cassunshine.thework.assets.elements.JournalLayoutElement;
import cassunshine.thework.assets.elements.NodeElement;
import cassunshine.thework.assets.elements.TextElement;
import cassunshine.thework.client.rendering.alchemy.AlchemyCircleRenderer;
import cassunshine.thework.client.rendering.util.RenderingUtilities;
import cassunshine.thework.client.utils.ClientJournalPage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.ArrayList;

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

        ArrayList<NodeElement> nodes = new ArrayList<>();

        for (JournalLayoutElement element : pageLayout.elements) {
            RenderingUtilities.pushMat();

            RenderingUtilities.translateMatrix(element.x, element.y, 0);

            if (element instanceof TextElement text) {
                renderText(text, element.width, element.height);
            } else if (element instanceof IconElement icon) {
                renderIcon(icon, element.width, element.height);
            } else if (element instanceof NodeElement node) {
                nodes.add(node);
            }
            RenderingUtilities.popMat();
        }

        for (NodeElement node : nodes) {
            RenderingUtilities.pushMat();
            RenderingUtilities.translateMatrix(node.x, node.y, 0);
            renderNode(node, node.width, node.height);
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
        RenderingUtilities.getMatStack().multiplyPositionMatrix(new Matrix4f().scale(1, -1, 0.001f));
        RenderingUtilities.scaleMatrix(width, height, width);
        RenderingUtilities.translateMatrix(0, 0, 0.16f);
        RenderingUtilities.renderItem(element.iconStack, ModelTransformationMode.GUI, null);
    }

    private void renderNode(NodeElement element, int width, int height) {
        RenderingUtilities.resetLightOverlay();
        RenderingUtilities.setupNormal(0, 0, 0);
        RenderingUtilities.setupColor(0xFF000000);

        RenderingUtilities.pushMat();

        RenderingUtilities.rotateMatrix(0, 0, MathHelper.PI);
        RenderingUtilities.rotateMatrix(MathHelper.HALF_PI, 0, 0);
        RenderingUtilities.scaleMatrix(width, width, width);

        AlchemyCircleRenderer.drawSidedCircleAndRune(0.5f, element.sideCount, element.runeId);
        AlchemyCircleRenderer.runDeferTasks();

        RenderingUtilities.popMat();
    }
}
