package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.client.gui.ingame.notebook.drawables.NodeDrawer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AlchemistNotebookNodePage extends AlchemistNotebookPage {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "node_page");

    private ButtonWidget circleSidesLeftButton;
    private ButtonWidget circleSidesRightButton;
    private TextWidget circleSidesLabel = new TextWidget(Text.translatable("ui.alchemist_notebook.page.beta.sides"), MinecraftClient.getInstance().textRenderer);

    private ButtonWidget circleRuneLeftButton;
    private ButtonWidget circleRuneRightButton;
    private TextWidget circleRuneLabel = new TextWidget(Text.translatable("ui.alchemist_notebook.page.beta.rune"), MinecraftClient.getInstance().textRenderer);

    public NodeDrawer drawnNode;

    private NbtCompound drawnNodeNbt;

    public AlchemistNotebookNodePage(NbtCompound compound) {
        super(compound);
    }

    public AlchemistNotebookNodePage() {
        super(IDENTIFIER, null);
    }

    @Override
    public void init(AlchemistNotebookScreen screen, int x, int y, int width, int height) {
        super.init(screen, x, y, width, height);

        var nbt = screen.stack.getOrCreateNbt();

        drawnNodeNbt = nbt.contains("node", NbtElement.COMPOUND_TYPE) ? nbt.getCompound("node") : new NbtCompound();
        var sides = drawnNodeNbt.contains("sides", NbtElement.INT_TYPE) ? drawnNodeNbt.getInt("sides") : 8;
        var runeID = drawnNodeNbt.contains("rune_id", NbtElement.INT_TYPE) ? drawnNodeNbt.getInt("rune_id") : 0;

        drawnNodeNbt.putInt("sides", sides);
        drawnNodeNbt.putInt("rune_id", runeID);
        nbt.put("node", drawnNodeNbt);

        drawnNode = new NodeDrawer(sides, runeID);

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
        drawnNode.color = 0xFF000000;

        //screen.addDrawable(circleSidesLabel);
        screen.addDrawableChild(circleSidesLeftButton);
        screen.addDrawableChild(circleSidesRightButton);

        //screen.addDrawableChild(circleRuneLabel);
        screen.addDrawableChild(circleRuneLeftButton);
        screen.addDrawableChild(circleRuneRightButton);

        screen.addDrawable(drawnNode);

        drawnNode.size = MathHelper.floor(width * 0.6f);

        drawnNode.x = x + width / 2;
        drawnNode.y = y + height / 3;


        circleSidesLeftButton.setX(drawnNode.x - circleSidesLeftButton.getWidth());
        circleSidesLeftButton.setY(drawnNode.y + drawnNode.size / 2 + 5);
        circleSidesRightButton.setX(drawnNode.x + circleSidesRightButton.getWidth() / 4);
        circleSidesRightButton.setY(drawnNode.y + drawnNode.size / 2 + 5);

        circleSidesLabel.alignCenter();
        //circleSidesLabel.setWidth(drawnNode.size);
        circleSidesLabel.setX(drawnNode.x - circleSidesLabel.getWidth() / 2 + 2);
        circleSidesLabel.setY(circleSidesRightButton.getY() + circleSidesRightButton.getHeight() / 2 - circleSidesLabel.getHeight() / 2);

        circleRuneLeftButton.setX(drawnNode.x - circleRuneLeftButton.getWidth());
        circleRuneLeftButton.setY(drawnNode.y + drawnNode.size / 2 + circleSidesLeftButton.getHeight());
        circleRuneRightButton.setX(drawnNode.x + circleRuneRightButton.getWidth() / 4);
        circleRuneRightButton.setY(circleRuneLeftButton.getY());

        circleRuneLabel.alignCenter();
        //circleRuneLabel.setWidth(drawnNode.size);
        circleRuneLabel.setX(drawnNode.x - circleRuneLabel.getWidth() / 2 + 2);
        circleRuneLabel.setY(circleRuneRightButton.getY() + circleRuneRightButton.getHeight() / 2 - circleRuneLabel.getHeight() / 2);
    }

    private void drawTextCustom(DrawContext context, TextWidget widget) {
        Text text = widget.getMessage();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int widgetX = widget.getX() + Math.round(0.5f * (float) (widget.getWidth() - textRenderer.getWidth(text)));
        int widgetY = widget.getY() + (widget.getHeight() - 9) / 2;
        OrderedText orderedText = text.asOrderedText();
        context.drawText(textRenderer, orderedText, widgetX, widgetY, 0xFF000000, false);
    }

    @Override
    public void render(AlchemistNotebookScreen screen, DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(screen, context, mouseX, mouseY, delta);

        drawTextCustom(context, circleSidesLabel);
        drawTextCustom(context, circleRuneLabel);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);


        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }
}
