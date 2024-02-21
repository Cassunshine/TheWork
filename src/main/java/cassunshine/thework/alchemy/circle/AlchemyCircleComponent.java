package cassunshine.thework.alchemy.circle;

import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;

public interface AlchemyCircleComponent {


    /**
     * Called when the alchemy circle activates.
     */
    void activate();

    /**
     * Called each tick that the circle is active.
     */
    void activeTick();

    /**
     * Called when the alchemy circle deactivates.
     */
    void deactivate();

    void onDestroy();

    NbtCompound writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);

    TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context);

    TheWorkNetworkEvent generateInteractEvent(ItemUsageContext context);

    void regenerateInteractionPoints(AlchemyCircleBlockEntity blockEntity);
}
