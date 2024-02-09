package cassunshine.thework.alchemy.circle.events.circle;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntityEvent;
import net.minecraft.util.Identifier;

public class AlchemyCircleEvent extends AlchemyCircleBlockEntityEvent {
    public AlchemyCircleEvent(Identifier id) {
        super(id);
    }

    public AlchemyCircleEvent(AlchemyCircle circle, Identifier id) {
        super(circle.blockEntity, id);
    }
}
