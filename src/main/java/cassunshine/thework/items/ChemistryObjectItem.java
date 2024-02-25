package cassunshine.thework.items;

import cassunshine.thework.alchemy.chemistry.ChemistryObjects;
import cassunshine.thework.blockentities.chemistry.ChemistrySetBlockEntity;
import cassunshine.thework.blocks.TheWorkBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class ChemistryObjectItem extends Item {

    public Identifier objectIdentifier;

    public ChemistryObjectItem(Identifier objectIdentifier) {
        super(new FabricItemSettings());
        this.objectIdentifier = objectIdentifier;
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        /*var position = context.getBlockPos().add(context.getSide().getVector());
        var blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
        ChemistrySetBlockEntity chemistrySet = null;

        //Try to place a bock entity, if none was found.
        if (!(blockEntity instanceof ChemistrySetBlockEntity)) {
            if (!TheWorkBlocks.CHEMISTRY_SET_BLOCK.getDefaultState().canPlaceAt(context.getWorld(), position))
                return ActionResult.PASS;

            context.getWorld().setBlockState(position, TheWorkBlocks.CHEMISTRY_SET_BLOCK.getDefaultState());

            blockEntity = context.getWorld().getBlockEntity(position);

            if (!(blockEntity instanceof ChemistrySetBlockEntity))
                return ActionResult.PASS;

            chemistrySet = (ChemistrySetBlockEntity) blockEntity;
        }

        if (chemistrySet == null)
            return ActionResult.PASS;

        var slot = chemistrySet.localPositionToSlot(chemistrySet.getLocalPos(context.getHitPos()));

        //If there is no object, try to put one there.
        if (slot.object == null) {
            var slotObject = ChemistryObjects.generateObject(objectIdentifier);

            if (slotObject != null) {
                slot.object = slotObject;
                chemistrySet.bakeShape();
                context.getPlayer().getStackInHand(context.getHand()).decrement(1);
                return ActionResult.SUCCESS;
            }
        }*/

        return ActionResult.PASS;
    }
}
