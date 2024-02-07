package cassunshine.thework.blockentities.alchemy_circle.nodes.types;

import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleComponent;
import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class AlchemyNodeType {

    /**
     * The ID of this node type.
     */
    public Identifier id;

    public boolean requireInteractionEntity = false;

    public boolean validityCheck(AlchemyNode node) {
        return true;
    }

    public void activate(AlchemyNode node) {

    }

    /**
     * Called once each tick for each node that has this type.
     */
    public void operate(AlchemyNode node) {
        //Output from inventory to outputs.
        node.inventory.transferAll(node.nextNodeOutput, 1);
        if (node.ring.hasNextRing) node.inventory.transferAll(node.parallelOutput, 1);
    }

    public void stop(AlchemyNode node) {

    }

    /**
     * Handles interaction when the player right-clicks a node with this type.
     */
    public boolean handleInteraction(AlchemyNode node, ItemUsageContext context) {
        return false;
    }
}
