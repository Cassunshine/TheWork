package cassunshine.thework.network.events;

import cassunshine.thework.blockentities.alchemy_circle.events.circle.ActivateCircleEvent;
import cassunshine.thework.blockentities.alchemy_circle.events.circle.FullSyncEvent;
import cassunshine.thework.blockentities.alchemy_circle.events.circle.SetCircleOutwardEvent;
import cassunshine.thework.blockentities.alchemy_circle.events.node.NodeSwapItemEvent;
import cassunshine.thework.blockentities.alchemy_circle.events.node.UpdateRuneOrTypeEvent;
import cassunshine.thework.blockentities.alchemy_circle.events.ring.SetRingClockwiseEvent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.function.Supplier;

public class TheWorkNetworkEvents {

    public static final HashMap<Identifier, Supplier<TheWorkNetworkEvent>> EVENT_FACTORIES = new HashMap<>();

    public static TriConsumer<BlockPos, World, TheWorkNetworkEvent> clientEventConsumer;

    public static final TheWorkNetworkEvent NONE = null;

    public static void initialize() {

        //Node
        register(UpdateRuneOrTypeEvent.IDENTIFIER, UpdateRuneOrTypeEvent::new);
        register(NodeSwapItemEvent.IDENTIFIER, NodeSwapItemEvent::new);

        //Ring
        register(SetRingClockwiseEvent.IDENTIFIER, SetRingClockwiseEvent::new);

        //Circle
        register(FullSyncEvent.IDENTIFIER, FullSyncEvent::new);
        register(ActivateCircleEvent.IDENTIFIER, ActivateCircleEvent::new);
        register(SetCircleOutwardEvent.IDENTIFIER, SetCircleOutwardEvent::new);

        //Maybe need this someday?
        /*for (var entry : TheWorkNetworkEvents.EVENT_FACTORIES.entrySet()) {
            ServerPlayNetworking.registerGlobalReceiver(entry.getKey(), (server, player, handler, buf, responseSender) -> {
                //Read event packet
                var event = entry.getValue().get();
                event.readPacket(buf);

                //Run event on server.
                server.execute(() -> {
                    event.applyToWorld(player.getWorld());
                });
            });
        }*/
    }


    private static void register(Identifier id, Supplier<TheWorkNetworkEvent> factory) {
        EVENT_FACTORIES.put(id, factory);
    }


    public static void sendEvent(BlockPos position, World world, TheWorkNetworkEvent event) {
        //Do nothing if the world doesn't exist, or if the event is NONE.
        if (world == null || event == NONE)
            return;

        //Only send packet on server.
        if (world.isClient) {
            if (clientEventConsumer != null)
                clientEventConsumer.accept(position, world, event);
            return;
        }

        //If running on logical server, relay to players.

        event.applyToWorld(world);

        //Write packet.
        var packet = PacketByteBufs.create();
        event.write(packet);

        //Send each player who can see the event the packet.
        for (var player : PlayerLookup.tracking((ServerWorld) world, position))
            ServerPlayNetworking.send(player, event.id, packet);
    }
}
