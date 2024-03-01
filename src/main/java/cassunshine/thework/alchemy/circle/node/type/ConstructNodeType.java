package cassunshine.thework.alchemy.circle.node.type;

import cassunshine.thework.alchemy.circle.node.AlchemyNode;

public class ConstructNodeType extends AlchemyNodeType {

    @Override
    public void activeTick(AlchemyNode node) {
        //Do not transfer into output, so don't call base.
    }
}
