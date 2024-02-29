package cassunshine.thework.network.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WitnessEvent extends BlockPosEvent {

    public float range = 32;

    public WitnessEvent(Identifier id) {
        super(id);
    }

    public WitnessEvent(BlockPos pos, Identifier id) {
        super(pos, id);
    }

    @Override
    public void applyToWorld(World world) {
        super.applyToWorld(world);

        var sqrDist = range * range;
        var center = position.toCenterPos();

        for (PlayerEntity player : world.getPlayers())
            if (player.getPos().squaredDistanceTo(center) <= sqrDist)
                applyToPlayer(player);
    }

    public void applyToPlayer(PlayerEntity e) {

    }
}
