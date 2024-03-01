package cassunshine.thework.client.networking;

import cassunshine.thework.client.events.TheWorkClientNetworkEvents;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.network.TheWorkNetworking;
import cassunshine.thework.data.recipes.TheWorkRecipes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;

public class TheWorkClientNetworking {

    public static void initialize() {
        TheWorkClientNetworkEvents.initialize();

        ClientPlayNetworking.registerGlobalReceiver(TheWorkNetworking.OPEN_ALCHEMIST_BOOK, TheWorkClientNetworking::onOpenBook);
        ClientPlayNetworking.registerGlobalReceiver(TheWorkNetworking.SYNC_ALL_DATA, TheWorkClientNetworking::syncAllData);
    }

    private static void onOpenBook(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        var stack = MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND);

        if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM))
            stack = MinecraftClient.getInstance().player.getStackInHand(Hand.OFF_HAND);

        if (!stack.isOf(TheWorkItems.ALCHEMIST_NOTEBOOK_ITEM))
            return;

        net.minecraft.item.ItemStack finalStack = stack;
        minecraftClient.execute(() -> {
            minecraftClient.setScreen(new AlchemistNotebookScreen(finalStack));
        });
    }

    public static void updateBook(NbtCompound compound) {
        var buf = PacketByteBufs.create();
        buf.writeNbt(compound);

        ClientPlayNetworking.send(TheWorkNetworking.CLIENT_UPDATED_NOTEBOOK, buf);
    }

    private static void syncAllData(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        //Don't sync when using integrated server.
        if (MinecraftClient.getInstance().isIntegratedServerRunning())
            return;

        TheWorkRecipes.readSync(buf);
    }
}
