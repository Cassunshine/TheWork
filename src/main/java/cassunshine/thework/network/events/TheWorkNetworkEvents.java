package cassunshine.thework.network.events;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.events.circle.ActivateToggleEvent;
import cassunshine.thework.alchemy.circle.events.circle.AddRingEvent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetItemEvent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetTypeAndRuneEvent;
import cassunshine.thework.alchemy.circle.events.ring.AlchemyRingClockwiseSetEvent;
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

    public static final TheWorkNetworkEvent NONE = new EmptyNetworkEvent(new Identifier(TheWorkMod.ModID, "none"));
    public static final TheWorkNetworkEvent SUCCESS = new EmptyNetworkEvent(new Identifier(TheWorkMod.ModID, "success"));

    public static void initialize() {

        register(AddRingEvent.IDENTIFIER, AddRingEvent::new);
        register(ActivateToggleEvent.IDENTIFIER, ActivateToggleEvent::new);

        register(AlchemyRingClockwiseSetEvent.IDENTIFIER, AlchemyRingClockwiseSetEvent::new);

        register(AlchemyNodeSetTypeAndRuneEvent.IDENTIFIER, AlchemyNodeSetTypeAndRuneEvent::new);
        register(AlchemyNodeSetItemEvent.IDENTIFIER, AlchemyNodeSetItemEvent::new);
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
        event.writePacket(packet);

        //Send each player who can see the event the packet.
        for (var player : PlayerLookup.tracking((ServerWorld) world, position))
            ServerPlayNetworking.send(player, event.id, packet);
    }
}
