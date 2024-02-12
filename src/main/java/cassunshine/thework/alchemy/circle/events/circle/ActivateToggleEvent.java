package cassunshine.thework.alchemy.circle.events.circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import net.minecraft.util.Identifier;

public class ActivateToggleEvent extends AlchemyCircleEvent {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "circle_activate_toggle");

    public ActivateToggleEvent() {
        super(IDENTIFIER);
    }

    public ActivateToggleEvent(AlchemyCircle circle) {
        super(circle, IDENTIFIER);
    }

    @Override
    public void applyToCircle(AlchemyCircle circle) {
        if (circle.isActive)
            circle.deactivate();
        else
            circle.activate();
    }
}
