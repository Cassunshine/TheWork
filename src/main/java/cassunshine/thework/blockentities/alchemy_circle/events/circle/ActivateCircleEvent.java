package cassunshine.thework.blockentities.alchemy_circle.events.circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import net.minecraft.util.Identifier;

public class ActivateCircleEvent extends FullSyncEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "events/activate_circle");

    public ActivateCircleEvent() {
        super(IDENTIFIER);
    }

    public ActivateCircleEvent(AlchemyCircleBlockEntity circle) {
        super(circle, IDENTIFIER);
    }

    @Override
    public void applyToCircle(AlchemyCircleBlockEntity target) {
        super.applyToCircle(target);

        target.isActive = !target.isActive;
    }
}
