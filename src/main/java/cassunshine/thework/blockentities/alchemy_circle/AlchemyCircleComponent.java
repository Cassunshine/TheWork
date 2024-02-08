package cassunshine.thework.blockentities.alchemy_circle;

import cassunshine.thework.network.events.TheWorkNetworkEvent;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;

/**
 * Common events shared between pieces of the alchemy circle will call recursively, so they're stored here.
 */
public interface AlchemyCircleComponent {


    void writeNbt(NbtCompound compound);

    void readNbt(NbtCompound compound);

    /**
     * Checks if the state of the component is valid to start a reaction.
     */
    boolean validityCheck();

    /**
     * Called when an alchemical reaction starts.
     */
    void activate();

    /**
     * Called once per tick on each component of an alchemy circle, while the circle is active.
     */
    void operate();

    /**
     * Called when an alchemical reaction stops
     */
    void stop();

    /**
     * Regenerates any required interaction points for the component.
     */
    void regenerateInteractionPoints();

    /**
     * Tries to handle a player right-clicking this circle.
     * <p>
     * May generate an interaction event, but itself does nothing.
     * Interaction event will be relayed to server and other clients to make sure everyone remains in sync.
     */
    TheWorkNetworkEvent generateInteractionEvent(ItemUsageContext context);

    /**
     * Handles an alchemy circle event to update the state of the alchemy circle.
     */
    boolean handleEvent(TheWorkNetworkEvent interaction);
}
