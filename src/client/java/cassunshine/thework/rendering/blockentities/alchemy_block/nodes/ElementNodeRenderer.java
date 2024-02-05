package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.blockentities.alchemy_circle.nodes.ElementNodeType;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class ElementNodeRenderer extends NodeTypeRenderer<ElementNodeType> {


    @Override
    public void render(ElementNodeType node) {
        var element = node.element;
        if (element == null)
            return;

        RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutoutNoCull(new Identifier(element.id.getNamespace(), "textures/item/" + element.id.getPath() + ".png")));

        RenderingUtilities.saneVertex(0, 0, 0, 255, 255, 255, 0, 0, 0, 1, 0);
        RenderingUtilities.saneVertex(0, 0, 1, 255, 255, 255, 0, 1, 0, 1, 0);
        RenderingUtilities.saneVertex(1, 0, 1, 255, 255, 255, 1, 1, 0, 1, 0);
        RenderingUtilities.saneVertex(1, 0, 0, 255, 255, 255, 1, 0, 0, 1, 0);
    }
}
