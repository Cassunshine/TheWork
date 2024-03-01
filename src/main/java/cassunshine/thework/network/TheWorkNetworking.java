package cassunshine.thework.network;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import cassunshine.thework.data.recipes.TheWorkRecipes;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class TheWorkNetworking {

    public static final Identifier SYNC_ALL_DATA = new Identifier(TheWorkMod.ModID, "sync_all_data");

    public static final Identifier OPEN_ALCHEMIST_BOOK = new Identifier(TheWorkMod.ModID, "open_book");
    public static final Identifier CLIENT_UPDATED_NOTEBOOK = new Identifier(TheWorkMod.ModID, "client_updated_notebook");

    public static void initialize() {
        TheWorkNetworkEvents.initialize();

        ServerPlayNetworking.registerGlobalReceiver(CLIENT_UPDATED_NOTEBOOK, TheWorkNetworking::onUpdateNotebook);
        ServerPlayConnectionEvents.INIT.register((handler, server) -> syncAllData(server, handler.player));
    }

    public static void openAlchemistBook(ServerPlayerEntity entity) {
        //Write packet.
        var packet = PacketByteBufs.create();

        ServerPlayNetworking.send(entity, OPEN_ALCHEMIST_BOOK, packet);

    }

    private static void onUpdateNotebook(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var stack = player.getStackInHand(Hand.MAIN_HAND);

        if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM))
            stack = player.getStackInHand(Hand.OFF_HAND);

        if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM))
            return;

        stack.setNbt(buf.readNbt());
    }

    //Syncs all data to all players on a server
    public static void syncAllData(MinecraftServer server) {
        for (ServerPlayerEntity entity : server.getPlayerManager().getPlayerList())
            syncAllData(server, entity);
    }

    public static void syncAllData(MinecraftServer server, ServerPlayerEntity target) {
        var packet = PacketByteBufs.create();

        TheWorkRecipes.writeSync(packet);

        ServerPlayNetworking.send(target, SYNC_ALL_DATA, packet);
    }
}
