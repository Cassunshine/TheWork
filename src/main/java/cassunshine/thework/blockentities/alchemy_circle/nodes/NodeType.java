package cassunshine.thework.blockentities.alchemy_circle.nodes;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Identifier;

public class NodeType {
    public Identifier id;

    public boolean requireEntity = false;


    public boolean handleInteraction(AlchemyNode target, ItemUsageContext context) {
        return true;
    }


    @Override
    public String toString() {
        return id.toString();
    }
}
