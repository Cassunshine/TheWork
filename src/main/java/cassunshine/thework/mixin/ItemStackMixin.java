package cassunshine.thework.mixin;

import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircles;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"), method = "useOnBlock", cancellable = true)
    public void thework_useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!AlchemyCircles.handleInteraction(context))
            return;

        cir.setReturnValue(ActionResult.PASS);
        cir.cancel();
    }

}
