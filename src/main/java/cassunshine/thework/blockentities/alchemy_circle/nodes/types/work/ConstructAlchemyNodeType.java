package cassunshine.thework.blockentities.alchemy_circle.nodes.types.work;

import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.AlchemyNodeType;

public class ConstructAlchemyNodeType extends AlchemyNodeType {

    @Override
    public boolean validityCheck(AlchemyNode node) {
        return node.ring.circle.constructLayout.recipe() != null;
    }

    @Override
    public void operate(AlchemyNode node) {
        //Don't move essence, this just accumulates it.
    }
}
