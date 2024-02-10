package cassunshine.thework.client.events;

import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TheWorkClientNetworkEvents {

    public static void initialize() {
        //Register packets for each of the network events.
        for (var entry : TheWorkNetworkEvents.EVENT_FACTORIES.entrySet()) {
            ClientPlayNetworking.registerGlobalReceiver(entry.getKey(), (client, handler, buf, responseSender) -> {
                //Generate event and read it on network thread.
                var event = entry.getValue().get();
                event.readPacket(buf);

                //Make the client process the event. it when available.
                client.execute(() -> event.applyToWorld(client.world));
            });
        }

        TheWorkNetworkEvents.clientEventConsumer = TheWorkClientNetworkEvents::onClientEvent;
    }

    private static void onClientEvent(BlockPos pos, World world, TheWorkNetworkEvent theWorkNetworkEvent) {
        //Send event to server.
        {
            var packet = PacketByteBufs.create();
            theWorkNetworkEvent.writePacket(packet);

            ClientPlayNetworking.send(theWorkNetworkEvent.id, packet);
        }
    }

}
