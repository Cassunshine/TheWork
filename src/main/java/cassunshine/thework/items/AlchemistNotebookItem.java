package cassunshine.thework.items;

import cassunshine.thework.network.TheWorkNetworking;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AlchemistNotebookItem extends Item {

    public AlchemistNotebookItem() {
        super(new FabricItemSettings());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient)
            return TypedActionResult.success(user.getStackInHand(hand));

        TheWorkNetworking.openAlchemistBook((ServerPlayerEntity) user);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
