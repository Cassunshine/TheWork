package cassunshine.thework.items.notebook;

import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.network.TheWorkNetworking;
import cassunshine.thework.recipes.TheWorkRecipes;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class AlchemistNotebookItem extends Item {

    public AlchemistNotebookItem() {
        super(new FabricItemSettings().maxCount(1));
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

        var targetBlock = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
        var item = targetBlock.asItem();

        var blockAdded = putItemIfNew(item, context.getStack());
        if (blockAdded != ActionResult.PASS)
            return blockAdded;

        var groundItems = context.getWorld().getEntitiesByClass(ItemEntity.class, new Box(context.getBlockPos().add(context.getSide().getVector())), p -> true);
        if (groundItems.isEmpty())
            return ActionResult.PASS;

        var groundItem = groundItems.get(context.getWorld().random.nextInt(groundItems.size())).getStack().getItem();

        return putItemIfNew(groundItem, context.getStack());
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }

    private ActionResult putItemIfNew(Item item, ItemStack stack) {
        var itemId = Registries.ITEM.getId(item);
        var stringId = itemId.toString();

        var recipe = TheWorkRecipes.getConstruction(item);
        if (recipe == null)
            return ActionResult.PASS;

        var nbt = stack.getOrCreateNbt();

        var recipeList = nbt.getList("recipeList", NbtElement.COMPOUND_TYPE);
        nbt.put("recipeList", recipeList);

        boolean isNew = true;
        for (int i = 0; i < recipeList.size() && isNew; i++) {
            var entry = recipeList.getCompound(i);

            if (entry.getString("id").equals(stringId))
                isNew = false;
        }

        if (isNew) {
            putRecipe(nbt, itemId, new NbtList());
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public static void putRecipe(NbtCompound nbt, Identifier itemId, NbtList guesses) {
        var recipeList = nbt.getList("recipeList", NbtElement.COMPOUND_TYPE);
        nbt.put("recipeList", recipeList);

        for (int i = 0; i < recipeList.size(); i++) {
            var recipeCompound = recipeList.getCompound(i);

            var id = new Identifier(recipeCompound.getString("id"));
            if (id.equals(itemId)) {
                recipeCompound.put("guess", guesses);
                return;
            }
        }

        {
            var recipeCompound = new NbtCompound();

            recipeCompound.putString("id", itemId.toString());
            recipeCompound.put("guess", guesses);
            recipeList.add(recipeCompound);
        }
    }
}
