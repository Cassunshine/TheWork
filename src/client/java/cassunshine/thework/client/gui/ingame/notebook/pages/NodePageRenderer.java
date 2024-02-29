package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.client.gui.ingame.notebook.drawables.NodeDrawer;
import cassunshine.thework.items.notebook.pages.NodePickPage;
import cassunshine.thework.client.rendering.util.RenderingUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class NodePageRenderer extends AlchemistNotebookPageRenderer<NodePickPage> {


    private ButtonWidget circleSidesLeftButton;
    private ButtonWidget circleSidesRightButton;
    private TextWidget circleSidesLabel = new TextWidget(Text.translatable("ui.alchemist_notebook.page.beta.sides"), MinecraftClient.getInstance().textRenderer);

    private ButtonWidget circleRuneLeftButton;
    private ButtonWidget circleRuneRightButton;
    private TextWidget circleRuneLabel = new TextWidget(Text.translatable("ui.alchemist_notebook.page.beta.rune"), MinecraftClient.getInstance().textRenderer);

    public NodeDrawer drawnNode;

    @Override
    public void init(NodePickPage target) {
        super.init(target);

        drawnNode = new NodeDrawer();

        drawnNode.color = 0xFF000000;

        //TODO - less magic numbers across this plz
        drawnNode.size = MathHelper.floor(PAGE_WIDTH * 0.6f);

        drawnNode.x = PAGE_WIDTH / 2;
        drawnNode.y = PAGE_HEIGHT / 3;
    }

    @Override
    public void addInteractables(AlchemistNotebookScreen screen, int x, int y) {
        circleSidesLeftButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/unselect"), new Identifier("transferable_list/unselect_highlighted")),
                b -> {
                    getTarget().sides = Math.max(getTarget().sides - 1, 3);
                    screen.syncNbt();
                },
                Text.empty()
        );
        circleSidesRightButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/select"), new Identifier("transferable_list/select_highlighted")),
                b -> {
                    getTarget().sides = Math.min(getTarget().sides + 1, 8);
                    screen.syncNbt();
                },
                Text.empty()
        );

        circleRuneLeftButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/unselect"), new Identifier("transferable_list/unselect_highlighted")),
                b -> {
                    getTarget().runeID = TheWorkRunes.getPreviousRune(getTarget().runeID);
                    screen.syncNbt();
                },
                Text.empty()
        );
        circleRuneRightButton = new TexturedButtonWidget(
                32, 32,
                new ButtonTextures(new Identifier("transferable_list/select"), new Identifier("transferable_list/select_highlighted")),
                b -> {
                    getTarget().runeID = (TheWorkRunes.getNextRune(getTarget().runeID));
                    screen.syncNbt();
                },
                Text.empty()
        );

        drawnNode.x = PAGE_WIDTH / 2;
        drawnNode.y = PAGE_HEIGHT / 3;

        circleSidesLabel.setTextColor(0xFFCFCFCF);
        circleRuneLabel.setTextColor(0xFFCFCFCF);

        //screen.addDrawable(circleSidesLabel);
        screen.addDrawableChild(circleSidesLeftButton);
        screen.addDrawableChild(circleSidesRightButton);

        //screen.addDrawableChild(circleRuneLabel);
        screen.addDrawableChild(circleRuneLeftButton);
        screen.addDrawableChild(circleRuneRightButton);

        //screen.addDrawable(drawnNode);


        circleSidesLabel.alignCenter();
        //circleSidesLabel.setWidth(drawnNode.size);
        circleSidesLabel.setX(drawnNode.x - circleSidesLabel.getWidth() / 2 + 2);
        circleSidesLabel.setY(drawnNode.y + drawnNode.size / 2 + 17);

        circleRuneLabel.alignCenter();
        //circleRuneLabel.setWidth(drawnNode.size);
        circleRuneLabel.setX(drawnNode.x - circleRuneLabel.getWidth() / 2 + 2);
        circleRuneLabel.setY(drawnNode.y + drawnNode.size / 2 + 51);

        circleSidesLeftButton.setX(x + circleSidesLabel.getX() - (circleSidesLabel.getWidth() + circleSidesLeftButton.getWidth()) / 2);
        circleSidesRightButton.setX(x + circleSidesLabel.getX() + (circleSidesLabel.getWidth() + circleSidesLeftButton.getWidth()) / 2);

        circleSidesLeftButton.setY(y + circleSidesLabel.getY() - (circleSidesLeftButton.getHeight() - circleSidesLabel.getHeight()) / 2);
        circleSidesRightButton.setY(y + circleSidesLabel.getY() - (circleSidesRightButton.getHeight() - circleSidesLabel.getHeight()) / 2);

        circleRuneLeftButton.setX(x + circleRuneLabel.getX() - (circleRuneLabel.getWidth() + circleRuneLeftButton.getWidth()) / 2);
        circleRuneRightButton.setX(x + circleRuneLabel.getX() + (circleRuneLabel.getWidth() + circleRuneLeftButton.getWidth()) / 2);

        circleRuneLeftButton.setY(y + circleRuneLabel.getY() - (circleRuneLeftButton.getHeight() - circleRuneLabel.getHeight()) / 2);
        circleRuneRightButton.setY(y + circleRuneLabel.getY() - (circleRuneRightButton.getHeight() - circleRuneLabel.getHeight()) / 2);

    }


    @Override
    public void render() {
        drawnNode.sides = getTarget().sides;
        drawnNode.runeID = getTarget().runeID;
        drawnNode.render();

        if (RenderingUtilities.SPACE == RenderingUtilities.RenderingSpace.GUI) {
            RenderingUtilities.drawText(circleSidesLabel, false);
            RenderingUtilities.drawText(circleRuneLabel, false);
        }
    }
}
