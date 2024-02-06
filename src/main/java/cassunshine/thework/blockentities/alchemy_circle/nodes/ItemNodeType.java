package cassunshine.thework.blockentities.alchemy_circle.nodes;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;

public class ItemNodeType extends NodeType {

    public ItemNodeType(){
        requireEntity = true;
    }

    @Override
    public boolean handleInteraction(AlchemyNode target, ItemUsageContext context) {
        var existing = target.item;
        var held = context.getStack();

        target.item = held.copyAndEmpty();

        context.getPlayer().equipStack(context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, existing);

        return false;
    }
}
