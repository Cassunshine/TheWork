package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.client.gui.ingame.notebook.drawables.NodeDrawer;
import cassunshine.thework.rendering.alchemy.AlchemyCircleRenderer;
import cassunshine.thework.rendering.util.RenderingUtilities;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AlchemistNotebookNodePage extends AlchemistNotebookPage {

    private ButtonWidget circleSidesLeftButton;
    private ButtonWidget circleSidesRightButton;
    private TextWidget circleSidesLabel = new TextWidget(Text.translatable("ui.alchemist_notebook.page.beta.sides"), MinecraftClient.getInstance().textRenderer);

    private ButtonWidget circleRuneLeftButton;
    private ButtonWidget circleRuneRightButton;
    private TextWidget circleRuneLabel = new TextWidget(Text.translatable("ui.alchemist_notebook.page.beta.rune"), MinecraftClient.getInstance().textRenderer);

    public NodeDrawer drawnNode;

    private NbtCompound drawnNodeNbt;

    public AlchemistNotebookNodePage(NbtCompound nbt) {
        drawnNodeNbt = nbt.contains("node", NbtElement.COMPOUND_TYPE) ? nbt.getCompound("node") : new NbtCompound();
        var sides = drawnNodeNbt.contains("sides", NbtElement.INT_TYPE) ? drawnNodeNbt.getInt("sides") : 8;
        var runeID = drawnNodeNbt.contains("rune_id", NbtElement.INT_TYPE) ? drawnNodeNbt.getInt("rune_id") : 0;

        drawnNodeNbt.putInt("sides", sides);
        drawnNodeNbt.putInt("rune_id", runeID);
        nbt.put("node", drawnNodeNbt);

        drawnNode = new NodeDrawer(sides, runeID);
        drawnNode.x = PAGE_WIDTH / 2;
        drawnNode.y = PAGE_HEIGHT / 3;
        drawnNode.color = 0xFF000000;

    }

    @Override
    public void addInteractables(AlchemistNotebookScreen screen, int x, int y) {
        circleSidesLeftButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/unselect"), new Identifier("transferable_list/unselect_highlighted")),
                b -> {
                    drawnNode.sides = Math.max(drawnNode.sides - 1, 3);

                    drawnNodeNbt.putInt("sides", drawnNode.sides);
                    screen.syncNbt();
                },
                Text.empty()
        );
        circleSidesRightButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/select"), new Identifier("transferable_list/select_highlighted")),
                b -> {
                    drawnNode.sides = Math.min(drawnNode.sides + 1, 8);

                    drawnNodeNbt.putInt("sides", drawnNode.sides);
                    screen.syncNbt();
                },
                Text.empty()
        );

        circleRuneLeftButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/unselect"), new Identifier("transferable_list/unselect_highlighted")),
                b -> {
                    drawnNode.setRuneID(drawnNode.runeID - 1 == -1 ? TheWorkRunes.getRuneCount() - 1 : drawnNode.runeID - 1);

                    drawnNodeNbt.putInt("rune_id", drawnNode.runeID);
                    screen.syncNbt();
                },
                Text.empty()
        );
        circleRuneRightButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/select"), new Identifier("transferable_list/select_highlighted")),
                b -> {
                    drawnNode.setRuneID((drawnNode.runeID + 1) % TheWorkRunes.getRuneCount());

                    drawnNodeNbt.putInt("rune_id", drawnNode.runeID);
                    screen.syncNbt();
                },
                Text.empty()
        );

        circleSidesLabel.setTextColor(0xFFCFCFCF);
        circleRuneLabel.setTextColor(0xFFCFCFCF);

        //screen.addDrawable(circleSidesLabel);
        screen.addDrawableChild(circleSidesLeftButton);
        screen.addDrawableChild(circleSidesRightButton);

        //screen.addDrawableChild(circleRuneLabel);
        screen.addDrawableChild(circleRuneLeftButton);
        screen.addDrawableChild(circleRuneRightButton);

        //screen.addDrawable(drawnNode);

        drawnNode.size = MathHelper.floor(PAGE_WIDTH * 0.6f);

        circleSidesLeftButton.setX(x + drawnNode.x - circleSidesLeftButton.getWidth());
        circleSidesLeftButton.setY(y + drawnNode.y + drawnNode.size / 2 + 5);
        circleSidesRightButton.setX(x + drawnNode.x + circleSidesRightButton.getWidth() / 4);
        circleSidesRightButton.setY(circleSidesLeftButton.getY());

        circleSidesLabel.alignCenter();
        //circleSidesLabel.setWidth(drawnNode.size);
        circleSidesLabel.setX(drawnNode.x - circleSidesLabel.getWidth() / 2 + 2);
        circleSidesLabel.setY(drawnNode.y + drawnNode.size / 2 + 17);

        circleRuneLeftButton.setX(x + drawnNode.x - circleRuneLeftButton.getWidth());
        circleRuneLeftButton.setY(y + drawnNode.y + drawnNode.size / 2 + circleSidesLeftButton.getHeight());
        circleRuneRightButton.setX(x + drawnNode.x + circleRuneRightButton.getWidth() / 4);
        circleRuneRightButton.setY(circleRuneLeftButton.getY());

        circleRuneLabel.alignCenter();
        //circleRuneLabel.setWidth(drawnNode.size);
        circleRuneLabel.setX(drawnNode.x - circleRuneLabel.getWidth() / 2 + 2);
        circleRuneLabel.setY(drawnNode.y + drawnNode.size / 2 + 44);

        drawnNode.x = PAGE_WIDTH / 2;
        drawnNode.y = PAGE_HEIGHT / 3;
    }


    @Override
    public void render() {
        drawnNode.render();

        if (RenderingUtilities.SPACE == RenderingUtilities.RenderingSpace.GUI) {
            RenderingUtilities.drawText(circleSidesLabel, false);
            RenderingUtilities.drawText(circleRuneLabel, false);
        }
    }
}
