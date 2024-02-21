package cassunshine.thework.client.networking;

import cassunshine.thework.client.events.TheWorkClientNetworkEvents;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookNodeScreen;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.network.TheWorkNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;

public class TheWorkClientNetworking {

    public static void initialize() {
        TheWorkClientNetworkEvents.initialize();

        ClientPlayNetworking.registerGlobalReceiver(TheWorkNetworking.OPEN_ALCHEMIST_BOOK, TheWorkClientNetworking::onOpenBook);
    }

    private static void onOpenBook(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        var stack = MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND);

        if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM))
            return;

        minecraftClient.execute(() -> {
            minecraftClient.setScreen(new AlchemistNotebookNodeScreen());
        });
    }
}
