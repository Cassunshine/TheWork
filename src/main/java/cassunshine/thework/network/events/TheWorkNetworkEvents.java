package cassunshine.thework.network.events;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.backfire.PlaceBlockBackfireEffect;
import cassunshine.thework.alchemy.circle.events.circle.ActivateToggleEvent;
import cassunshine.thework.alchemy.circle.events.circle.AddRingEvent;
import cassunshine.thework.alchemy.circle.events.circle.AlchemyCircleSetColorEvent;
import cassunshine.thework.alchemy.circle.events.circle.CreateLinkEvent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetColorEvent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetItemEvent;
import cassunshine.thework.alchemy.circle.events.node.AlchemyNodeSetSidesAndRune;
import cassunshine.thework.alchemy.circle.events.ring.AlchemyRingClockwiseSetEvent;
import cassunshine.thework.alchemy.circle.events.ring.AlchemyRingSetColorEvent;
import cassunshine.thework.network.events.bookevents.BookLearnEvent;
import cassunshine.thework.network.events.bookevents.DiscoverMechanicEvent;
import cassunshine.thework.network.events.bookevents.WitnessRecipeEvent;
import cassunshine.thework.network.events.effects.SpawnBoltEvent;
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

    public static final int MAX_OBSERVER_DISTANCE_SQR = 32 * 32;

    public static final HashMap<Identifier, Supplier<TheWorkNetworkEvent>> EVENT_FACTORIES = new HashMap<>();

    public static TriConsumer<BlockPos, World, TheWorkNetworkEvent> clientEventConsumer;

    public static final TheWorkNetworkEvent NONE = new EmptyNetworkEvent(new Identifier(TheWorkMod.ModID, "none"));
    public static final TheWorkNetworkEvent SUCCESS = new EmptyNetworkEvent(new Identifier(TheWorkMod.ModID, "success"));

    public static void initialize() {

        register(AddRingEvent.IDENTIFIER, AddRingEvent::new);
        register(CreateLinkEvent.IDENTIFIER, CreateLinkEvent::new);
        register(ActivateToggleEvent.IDENTIFIER, ActivateToggleEvent::new);

        register(AlchemyRingClockwiseSetEvent.IDENTIFIER, AlchemyRingClockwiseSetEvent::new);

        register(AlchemyNodeSetSidesAndRune.IDENTIFIER, AlchemyNodeSetSidesAndRune::new);
        register(AlchemyNodeSetItemEvent.IDENTIFIER, AlchemyNodeSetItemEvent::new);

        register(AlchemyCircleSetColorEvent.IDENTIFIER, AlchemyCircleSetColorEvent::new);
        register(AlchemyRingSetColorEvent.IDENTIFIER, AlchemyRingSetColorEvent::new);
        register(AlchemyNodeSetColorEvent.IDENTIFIER, AlchemyNodeSetColorEvent::new);

        register(PlaceBlockBackfireEffect.Event.IDENTIFIER, PlaceBlockBackfireEffect.Event::new);

        register(DiscoverMechanicEvent.IDENTIFIER, DiscoverMechanicEvent::new);
        register(WitnessRecipeEvent.IDENTIFIER, WitnessRecipeEvent::new);

        register(SpawnBoltEvent.IDENTIFIER, SpawnBoltEvent::new);
    }


    private static void register(Identifier id, Supplier<TheWorkNetworkEvent> factory) {
        EVENT_FACTORIES.put(id, factory);
    }


    public static void sendEvent(BlockPos position, World world, TheWorkNetworkEvent event) {
        //Do nothing if the world doesn't exist, or if the event is NONE.
        if (world == null || event == NONE || event == SUCCESS)
            return;

        //Only send packet on server.
        if (world.isClient) {
            if (clientEventConsumer != null)
                clientEventConsumer.accept(position, world, event);
            return;
        }

        //If running on logical server, run on self.
        event.applyToWorld(world);

        //Write packet.
        var packet = PacketByteBufs.create();
        event.writePacket(packet);

        //Send each player who can see the event the packet.
        for (var player : PlayerLookup.tracking((ServerWorld) world, position))
            ServerPlayNetworking.send(player, event.id, packet);
    }

    public static void sendBookLearnEvent(BlockPos pos, World world, BookLearnEvent event) {
        if (world == null || event == NONE || event == SUCCESS)
            return;

        if (world.isClient) {
            if (clientEventConsumer != null)
                clientEventConsumer.accept(pos, world, event);
            return;
        }

        //Write packet.
        var packet = PacketByteBufs.create();
        event.writePacket(packet);

        //For all players in range
        for (var player : PlayerLookup.tracking((ServerWorld) world, pos)) {
            if (pos.getSquaredDistance(player.getPos()) > MAX_OBSERVER_DISTANCE_SQR)
                continue;

            //Process on server for that player, then send to player.
            event.applyToPlayer(player);
            ServerPlayNetworking.send(player, event.id, packet);
        }
    }
}
