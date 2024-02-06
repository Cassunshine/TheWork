package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemy_circle.nodes.ElementNodeType;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class ElementNodeRenderer extends SpriteNodeRenderer {

    private Identifier id;

    @Override
    protected Identifier getSprite() {
        return id;
    }

    @Override
    public void render(AlchemyNode node) {
        var type = node.type;
        if (!(type instanceof ElementNodeType elementType))
            return;

        id = new Identifier(elementType.element.id.getNamespace(), "textures/item/" + elementType.element.id.getPath() + ".png");
        super.render(node);
    }
}
