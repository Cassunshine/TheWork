package cassunshine.thework.client.rendering.blockentities.alchemy_block.nodes;


import cassunshine.thework.alchemy.circle.node.AlchemyNode;

public class AlchemyNodeTypeRenderer {

    public int circleSides;
    public float extraRotationAngle;

    public void render(AlchemyNode node) {

    }

    public AlchemyNodeTypeRenderer withSides(int sides) {
        this.circleSides = sides;
        return this;
    }

    public AlchemyNodeTypeRenderer withRotation(float angle) {
        this.extraRotationAngle = angle;
        return this;
    }
}
