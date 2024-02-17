package cassunshine.thework.alchemy.circle.node.type;

import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.elements.Elements;

/**
 * Passes elements to the path only if they match the rune on the node.
 */
public class FilterNodeType extends AlchemyNodeType {

    @Override
    public void activeTick(AlchemyNode node) {
        var element = Elements.getElement(node.rune);

        //Move all elements except the filtered one into the output.
        node.inventory.transfer(node.output, Float.POSITIVE_INFINITY, e -> e != element);
    }
}
