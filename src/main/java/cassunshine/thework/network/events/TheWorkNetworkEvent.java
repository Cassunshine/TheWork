package cassunshine.thework.network.events;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TheWorkNetworkEvent {
    public final Identifier id;

    public TheWorkNetworkEvent(Identifier id) {
        this.id = id;
    }

    public void writePacket(PacketByteBuf buf) {
    }

    public void readPacket(PacketByteBuf buf) {
    }


    public void applyToWorld(World world) {

    }
}
