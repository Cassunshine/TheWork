package cassunshine.thework.items;

import cassunshine.thework.network.TheWorkNetworking;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AlchemistNotebookItem extends Item {

    public AlchemistNotebookItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient)
            return TypedActionResult.consume(user.getStackInHand(hand));

        TheWorkNetworking.openAlchemistBook((ServerPlayerEntity) user);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }


    public static void setRune(Identifier rune) {

    }

    public static void setType(Identifier type) {

    }
}
