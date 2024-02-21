package cassunshine.thework.network;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TheWorkNetworking {

    public static final Identifier OPEN_ALCHEMIST_BOOK = new Identifier(TheWorkMod.ModID, "open_book");

    public static void initialize() {
        TheWorkNetworkEvents.initialize();
    }

    public static void openAlchemistBook(ServerPlayerEntity entity) {
        //Write packet.
        var packet = PacketByteBufs.create();

        ServerPlayNetworking.send(entity, OPEN_ALCHEMIST_BOOK, packet);
    }
}
