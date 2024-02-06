package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class SpriteNodeRenderer extends NodeRenderer {

    protected Identifier getSprite() {
        return null;
    }

    @Override
    public void render(AlchemyNode node) {

        var sprite = getSprite();

        if(sprite == null)
            return;

        RenderingUtilities.translateMatrix(-0.5f, 0, -0.5f);

        RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutoutNoCull(getSprite()));

        RenderingUtilities.saneVertex(0, 0, 0, 255, 255, 255, 0, 0, 0, 1, 0);
        RenderingUtilities.saneVertex(0, 0, 1, 255, 255, 255, 0, 1, 0, 1, 0);
        RenderingUtilities.saneVertex(1, 0, 1, 255, 255, 255, 1, 1, 0, 1, 0);
        RenderingUtilities.saneVertex(1, 0, 0, 255, 255, 255, 1, 0, 0, 1, 0);

    }
}
