package cassunshine.thework.entities;

import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InteractionPointEntity extends Entity {

    private static final TrackedData<BlockPos> CIRCLE_POS = DataTracker.registerData(InteractionPointEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    public AlchemyCircleBlockEntity circle;

    public InteractionPointEntity(EntityType<?> type, World world) {
        super(TheWorkEntities.INTERACTION_POINT_TYPE, world);

        noClip = true;
        calculateDimensions();
    }

    public void setCircle(AlchemyCircleBlockEntity be) {
        circle = be;
        dataTracker.set(CIRCLE_POS, be.getPos());
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(CIRCLE_POS, new BlockPos(0, 0, 0));
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);

        if (data != CIRCLE_POS)
            return;

        var pos = dataTracker.get(CIRCLE_POS);
        var maybeCircle = getWorld().getBlockEntity(pos, TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE);

        circle = maybeCircle.orElse(null);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(1, 1 / 32.0f);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (circle == null)
            return ActionResult.PASS;

        //Create an ItemUsageContext to generate an event from.
        BlockHitResult bhr = (BlockHitResult) player.raycast(PlayerEntity.getReachDistance(player.isCreative()), 0, false);
        ItemUsageContext ctx = new ItemUsageContext(player.getWorld(), player, Hand.MAIN_HAND, player.getMainHandStack(), bhr);

        return AlchemyCircleBlockEntity.generateAndSendEvent(circle, ctx) ? ActionResult.SUCCESS : ActionResult.PASS;
    }
}
