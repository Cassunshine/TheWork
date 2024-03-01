package cassunshine.thework.client.networking.events;

import cassunshine.thework.alchemy.backfire.BackfireEffects;
import cassunshine.thework.alchemy.backfire.PlaceBlockBackfireEffect;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import cassunshine.thework.client.rendering.particles.BoltParticle;
import cassunshine.thework.network.events.bookevents.BookLearnEvent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
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

                if (event instanceof BackfireEffects.ElementalBackfireEvent backfireEvent) {
                    onBackfireEvent(backfireEvent);
                }

                if (event instanceof BookLearnEvent learnEvent) {
                    client.execute(() -> {
                        learnEvent.applyToPlayer(client.player);
                    });
                }
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

    private static void onBackfireEvent(BackfireEffects.ElementalBackfireEvent event) {
        if (event instanceof PlaceBlockBackfireEffect.Event blockBackfireEffect) {
            var client = MinecraftClient.getInstance();

            client.execute(() -> {
                client.particleManager.addParticle(new BoltParticle(
                        client.world,
                        blockBackfireEffect.originPos.x, blockBackfireEffect.originPos.y, blockBackfireEffect.originPos.z,
                        blockBackfireEffect.hitPos.x, blockBackfireEffect.hitPos.y, blockBackfireEffect.hitPos.z,
                        blockBackfireEffect.element
                ));
            });
        }
    }

}
