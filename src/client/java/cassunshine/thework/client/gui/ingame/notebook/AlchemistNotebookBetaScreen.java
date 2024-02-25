package cassunshine.thework.client.gui.ingame.notebook;

import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.client.gui.ingame.notebook.drawables.NodeDrawer;
import cassunshine.thework.network.TheWorkNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.handler.PacketBundler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class AlchemistNotebookBetaScreen extends AlchemistNotebookScreen {

    private final ButtonWidget circleSidesLeftButton;
    private final ButtonWidget circleSidesRightButton;
    private final TextWidget circleSidesLabel = new TextWidget(Text.translatable("ui.alchemist_notebook.page.beta.sides"), MinecraftClient.getInstance().textRenderer);

    private final ButtonWidget circleRuneLeftButton;
    private final ButtonWidget circleRuneRightButton;
    private final TextWidget circleRuneLabel = new TextWidget(Text.translatable("ui.alchemist_notebook.page.beta.rune"), MinecraftClient.getInstance().textRenderer);

    public final NodeDrawer drawnNode;

    private NbtCompound drawnNodeNbt;

    public AlchemistNotebookBetaScreen(ItemStack stack) {
        super(stack, Text.translatable("ui.alchemist_notebook.page.beta.title"), "beta");

        drawnNodeNbt = itemNbt.contains("node", NbtElement.COMPOUND_TYPE) ? itemNbt.getCompound("node") : new NbtCompound();
        var sides = drawnNodeNbt.contains("sides", NbtElement.INT_TYPE) ? drawnNodeNbt.getInt("sides") : 8;
        var runeID = drawnNodeNbt.contains("rune_id", NbtElement.INT_TYPE) ? drawnNodeNbt.getInt("rune_id") : 0;


        drawnNodeNbt.putInt("sides", sides);
        drawnNodeNbt.putInt("rune_id", runeID);
        itemNbt.put("node", drawnNodeNbt);

        drawnNode = new NodeDrawer(sides, runeID);

        circleSidesLeftButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/unselect"), new Identifier("transferable_list/unselect_highlighted")),
                b -> {
                    drawnNode.sides = Math.max(drawnNode.sides - 1, 3);

                    drawnNodeNbt.putInt("sides", drawnNode.sides);
                    syncNbt();
                },
                Text.empty()
        );
        circleSidesRightButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/select"), new Identifier("transferable_list/select_highlighted")),
                b -> {
                    drawnNode.sides = Math.min(drawnNode.sides + 1, 8);

                    drawnNodeNbt.putInt("sides", drawnNode.sides);
                    syncNbt();
                },
                Text.empty()
        );

        circleRuneLeftButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/unselect"), new Identifier("transferable_list/unselect_highlighted")),
                b -> {
                    drawnNode.setRuneID(drawnNode.runeID - 1 == -1 ? TheWorkRunes.getRuneCount() - 1 : drawnNode.runeID - 1);

                    drawnNodeNbt.putInt("rune_id", drawnNode.runeID);
                    syncNbt();
                },
                Text.empty()
        );
        circleRuneRightButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/select"), new Identifier("transferable_list/select_highlighted")),
                b -> {
                    drawnNode.setRuneID((drawnNode.runeID + 1) % TheWorkRunes.getRuneCount());

                    drawnNodeNbt.putInt("rune_id", drawnNode.runeID);
                    syncNbt();
                },
                Text.empty()
        );

        syncNbt();
    }

    @Override
    protected void init() {
        super.init();

        addDrawable(circleSidesLabel);
        addDrawableChild(circleSidesLeftButton);
        addDrawableChild(circleSidesRightButton);

        addDrawableChild(circleRuneLabel);
        addDrawableChild(circleRuneLeftButton);
        addDrawableChild(circleRuneRightButton);

        addDrawable(drawnNode);

        drawnNode.size = 60;

        drawnNode.x = width / 2;
        drawnNode.y = height / 2;

        circleSidesLeftButton.setX(drawnNode.x - circleSidesLeftButton.getWidth());
        circleSidesLeftButton.setY(drawnNode.y + drawnNode.size / 2);
        circleSidesRightButton.setX(drawnNode.x + circleSidesRightButton.getWidth() / 4);
        circleSidesRightButton.setY(drawnNode.y + drawnNode.size / 2);

        circleSidesLabel.alignCenter();
        circleSidesLabel.setWidth(drawnNode.size);
        circleSidesLabel.setX(drawnNode.x - circleSidesLabel.getWidth() / 2 + 2);
        circleSidesLabel.setY(circleSidesRightButton.getY() + circleSidesRightButton.getHeight() / 2 - circleSidesLabel.getHeight() / 2);

        circleRuneLeftButton.setX(drawnNode.x - circleRuneLeftButton.getWidth());
        circleRuneLeftButton.setY(drawnNode.y + drawnNode.size / 2 + circleSidesLeftButton.getHeight());
        circleRuneRightButton.setX(drawnNode.x + circleRuneRightButton.getWidth() / 4);
        circleRuneRightButton.setY(circleRuneLeftButton.getY());

        circleRuneLabel.alignCenter();
        circleRuneLabel.setWidth(drawnNode.size);
        circleRuneLabel.setX(drawnNode.x - circleRuneLabel.getWidth() / 2 + 2);
        circleRuneLabel.setY(circleRuneRightButton.getY() + circleRuneRightButton.getHeight() / 2 - circleRuneLabel.getHeight() / 2);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderInGameBackground(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, 0x3C0F0F0F, 0x3C0F0F0F);
    }

    private void syncNbt() {
        var pkt = PacketByteBufs.create();

        pkt.writeNbt(itemNbt);

        ClientPlayNetworking.send(TheWorkNetworking.CLIENT_UPDATED_NOTEBOOK, pkt);
    }
}
