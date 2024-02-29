package cassunshine.thework.client.gui.ingame.notebook.drawables;

import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.client.rendering.alchemy.AlchemyCircleRenderer;
import cassunshine.thework.client.rendering.util.RenderingUtilities;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class NodeDrawer implements Drawable {

    public int x, y;

    public int size;

    public int sides = 8;
    public Identifier runeID = TheWorkRunes.NULL;

    public int color;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderingUtilities.setupStack(context.getMatrices());
        RenderingUtilities.setupConsumers(context.getVertexConsumers());
        render();
    }

    public void render() {
        RenderingUtilities.setupRenderLayer(AlchemyCircleRenderer.getLayer());
        RenderingUtilities.setupNormal(0, 0, 1);
        RenderingUtilities.setupColor(color);

        RenderingUtilities.pushMat();

        RenderingUtilities.translateMatrix(x, y, 0);
        RenderingUtilities.rotateMatrix(0, 0, MathHelper.PI);
        RenderingUtilities.rotateMatrix(MathHelper.HALF_PI, 0, 0);
        RenderingUtilities.scaleMatrix(-size, size, size);

        AlchemyCircleRenderer.drawSidedCircleAndRune(0.5f, sides, runeID);
        AlchemyCircleRenderer.runDeferTasks();

        RenderingUtilities.popMat();
    }
}
