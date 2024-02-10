package cassunshine.thework.alchemy.circle;

import cassunshine.thework.network.events.TheWorkNetworkEvent;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;

public interface AlchemyCircleComponent {

    NbtCompound writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);

    TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context);

    TheWorkNetworkEvent generateInteractEvent(ItemUsageContext context);
}
