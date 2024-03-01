package cassunshine.thework.items.notebook;

import cassunshine.thework.network.TheWorkNetworking;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class AlchemistNotebookItem extends Item {
    private static final ThreadLocal<NotebookData> INTERACTION_CACHED_DATA = new ThreadLocal<>();

    public AlchemistNotebookItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    private static NotebookData getData() {
        var val = INTERACTION_CACHED_DATA.get();
        if (val == null)
            INTERACTION_CACHED_DATA.set(val = new NotebookData());

        return val;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.consume(user.getStackInHand(hand));

        TheWorkNetworking.openAlchemistBook((ServerPlayerEntity) user);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getPlayer().isSneaking())
            return ActionResult.PASS;

        var world = context.getWorld();
        var data = getData();
        data.readNbt(context.getStack().getOrCreateNbt());

        var targetBlock = world.getBlockState(context.getBlockPos()).getBlock();
        var item = targetBlock.asItem();

        var blockAdded = data.recipesSection.putItemIfNew(item);
        if (blockAdded != ActionResult.PASS) {
            context.getStack().setNbt(data.writeNbt(new NbtCompound()));
            if (world.isClient)
                world.playSound(context.getPlayer(), context.getPlayer().getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.NEUTRAL);
            return blockAdded;
        }

        var groundItems = world.getEntitiesByClass(ItemEntity.class, new Box(context.getBlockPos().add(context.getSide().getVector())), p -> true);
        if (groundItems.isEmpty())
            return ActionResult.PASS;

        var groundItem = groundItems.get(world.random.nextInt(groundItems.size())).getStack().getItem();

        var itemAdded = data.recipesSection.putItemIfNew(groundItem);
        if (itemAdded != ActionResult.PASS) {
            context.getStack().setNbt(data.writeNbt(new NbtCompound()));
            if (world.isClient)
                world.playSound(context.getPlayer(), context.getPlayer().getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.NEUTRAL);
            return itemAdded;
        }

        return ActionResult.PASS;
    }
}
