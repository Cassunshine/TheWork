package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.blockentities.alchemycircle.nodes.AlchemyNode;

public class AlchemyNodeTypeRenderer {

    public int circleSides;

    public void render(AlchemyNode node) {

    }

    public AlchemyNodeTypeRenderer withSides(int sides) {
        this.circleSides = sides;
        return this;
    }
}
