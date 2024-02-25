package cassunshine.thework.network;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class TheWorkNetworking {

    public static final Identifier OPEN_ALCHEMIST_BOOK = new Identifier(TheWorkMod.ModID, "open_book");
    public static final Identifier CLIENT_UPDATED_NOTEBOOK = new Identifier(TheWorkMod.ModID, "client_updated_notebook");

    public static void initialize() {
        TheWorkNetworkEvents.initialize();
    }

    public static void openAlchemistBook(ServerPlayerEntity entity) {
        //Write packet.
        var packet = PacketByteBufs.create();

        ServerPlayNetworking.send(entity, OPEN_ALCHEMIST_BOOK, packet);

        ServerPlayNetworking.registerGlobalReceiver(CLIENT_UPDATED_NOTEBOOK, TheWorkNetworking::onUpdateNotebook);
    }

    private static void onUpdateNotebook(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var stack = player.getStackInHand(Hand.MAIN_HAND);

        if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM))
            stack = player.getStackInHand(Hand.OFF_HAND);

        if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM))
            return;

        stack.setNbt(buf.readNbt());
    }
}
