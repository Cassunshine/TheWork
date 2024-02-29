package cassunshine.thework.client.gui.ingame.notebook.drawables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.util.Identifier;

public class TextureDrawer implements Drawable {

    public Identifier textureId;

    public int x, y;
    public int width, height;

    public int u, v;
    public int uWidth, vHeight;

    public int textureWidth, textureHeight;

    public TextureDrawer(Identifier textureId, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        this.textureId = textureId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.u = u;
        this.v = v;
        this.uWidth = uWidth;
        this.vHeight = vHeight;

        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(textureId,
                x, y, width, height,
                u, v, uWidth, vHeight,
                textureWidth, textureHeight
        );
    }
}
