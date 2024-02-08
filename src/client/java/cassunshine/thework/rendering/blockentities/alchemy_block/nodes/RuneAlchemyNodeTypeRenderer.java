package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

/**
 * Renders a rune along with the alchemy node.
 */
public class RuneAlchemyNodeTypeRenderer extends AlchemyNodeTypeRenderer {

    @Override
    public void render(AlchemyNode node) {
        var sprite = node.rune;
        if (sprite == null || sprite.getPath().equals("none"))
            return;

        //Modify rune to point to rune texture.
        sprite = sprite.withPath("textures/runes/" + sprite.getPath() + ".png");

        //Move to center of rune.
        RenderingUtilities.translateMatrix(-0.5f, 0, -0.5f);

        RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutoutNoCull(sprite));

        RenderingUtilities.saneVertex(0, 0, 0, 255, 255, 255, 0, 0, 0, 1, 0);
        RenderingUtilities.saneVertex(0, 0, 1, 255, 255, 255, 0, 1, 0, 1, 0);
        RenderingUtilities.saneVertex(1, 0, 1, 255, 255, 255, 1, 1, 0, 1, 0);
        RenderingUtilities.saneVertex(1, 0, 0, 255, 255, 255, 1, 0, 0, 1, 0);

    }
}