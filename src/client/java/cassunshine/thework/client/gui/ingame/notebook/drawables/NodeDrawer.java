package cassunshine.thework.client.gui.ingame.notebook.drawables;

import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.rendering.alchemy.AlchemyCircleRenderer;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class NodeDrawer implements Drawable {

    public int x, y;

    public int size;

    public int sides = 0;
    public int runeID = 0;

    private Identifier rune;

    public NodeDrawer(int sides, int runeID) {
        setSides(sides);
        setRuneID(runeID);
    }

    public void setSides(int sides) {
        this.sides = sides;
    }

    public void setRuneID(int runeID) {
        this.runeID = runeID;
        this.rune = TheWorkRunes.getRuneByID(runeID);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderingUtilities.setupStack(context.getMatrices());
        RenderingUtilities.setupConsumers(context.getVertexConsumers());
        RenderingUtilities.setupRenderLayer(AlchemyCircleRenderer.getLayer());
        RenderingUtilities.setupNormal(0, 0, 1);
        RenderingUtilities.setupColor(255, 255, 255, 255);

        RenderingUtilities.pushMat();

        RenderingUtilities.translateMatrix(x, y, 0);
        RenderingUtilities.rotateMatrix(0, 0, MathHelper.PI);
        RenderingUtilities.rotateMatrix(MathHelper.HALF_PI, 0, 0);
        RenderingUtilities.scaleMatrix(size, size, size);

        AlchemyCircleRenderer.drawSidedCircleAndRune(0.5f, sides, rune);
        AlchemyCircleRenderer.runDeferTasks();

        RenderingUtilities.popMat();
    }
}
