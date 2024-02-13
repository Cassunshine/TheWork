package cassunshine.thework.blockentities.chemistry;

import cassunshine.thework.alchemy.chemistry.ChemistryObject;
import cassunshine.thework.alchemy.chemistry.ChemistryObjects;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.items.ChemistryObjectItem;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.utils.TheWorkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ChemistrySetBlockEntity extends BlockEntity {

    public VoxelShape currentShape = VoxelShapes.cuboid(0, 0, 0, 1, 1, 1);

    public final ChemistryObjectSlot[] objectSlots = new ChemistryObjectSlot[]{
            new ChemistryObjectSlot(new Vec3d(0, 0, 0)),
            new ChemistryObjectSlot(new Vec3d(0.5f, 0, 0)),
            new ChemistryObjectSlot(new Vec3d(0, 0, 0.5f)),
            new ChemistryObjectSlot(new Vec3d(0.5f, 0, 0.5f)),
    };

    public ChemistrySetBlockEntity(BlockPos pos, BlockState state) {
        super(TheWorkBlockEntities.CHEMISTRY_SET_TYPE, pos, state);
    }

    public void tick() {

    }

    public Vec3d getLocalPos(Vec3d pos) {
        return pos.subtract(getPos().getX(), getPos().getY(), getPos().getZ());
    }


    public ChemistryObjectSlot localPositionToSlot(Vec3d pos) {
        int index = 0;

        if (pos.x > 0.5f) index++;
        if (pos.z > 0.5f) index += 2;

        return objectSlots[index];
    }

    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        Vec3d localPosition = getLocalPos(hit.getPos());
        ItemStack stack = player.getStackInHand(hand);

        var slot = localPositionToSlot(localPosition);

        //If sneaking, pick up the slot, if it has an object.
        if (player.isSneaking() && slot.object != null) {
            TheWorkUtils.dropItem(world, new ItemStack(TheWorkItems.getItem(slot.object)), hit.getPos().x, hit.getPos().y, hit.getPos().z);
            slot.object = null;
            return ActionResult.SUCCESS;
        }

        //If not sneaking, but there is an object, attempt to interact with it.
        if (slot.object != null) {
            var slotResult = slot.object.onUse(player, hand, hit, localPosition.subtract(slot.rootPos));
            if (slotResult != ActionResult.PASS) return slotResult;
        }

        return ActionResult.PASS;
    }

    public void bakeShape() {
        VoxelShape newShape = null;

        for (ChemistryObjectSlot slot : objectSlots) {
            if (slot.object == null) continue;

            if (newShape == null)
                newShape = slot.object.getShape().offset(slot.rootPos.x, slot.rootPos.y, slot.rootPos.z);
            else
                newShape = VoxelShapes.union(newShape, slot.object.getShape().offset(slot.rootPos.x, slot.rootPos.y, slot.rootPos.z));
        }


        currentShape = newShape == null ? VoxelShapes.cuboid(0, 0, 0, 1, 1, 1) : newShape;
    }


    public class ChemistryObjectSlot {
        public ChemistryObject object = null;
        public int orientation = 0;

        public Vec3d rootPos;

        public ChemistryObjectSlot(Vec3d rootPos) {
            this.rootPos = rootPos;
        }
    }
}
