package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.TheWorkClient;
import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.alchemy.runes.TheWorkRunes;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;
import cassunshine.thework.items.notebook.pages.RecipePage;
import cassunshine.thework.client.rendering.alchemy.AlchemyCircleRenderer;
import cassunshine.thework.client.rendering.util.RenderingUtilities;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class RecipePageRenderer extends AlchemistNotebookPageRenderer<RecipePage> {

    @Override
    public void init(RecipePage target) {
        super.init(target);
    }

    @Override
    public void addInteractables(AlchemistNotebookScreen screen, int x, int y) {
        super.addInteractables(screen, x, y);

        var target = getTarget();

        if (target.isCorrect)
            return;

        int baseX = x + AlchemistNotebookPage.PAGE_WIDTH / 2;
        int baseY = y + AlchemistNotebookPage.PAGE_HEIGHT / 2;

        for (RecipePage.RingGuess ringGuess : target.circleGuess) {
            var runes = ringGuess.runeGuesses;

            for (int i = 0; i < runes.size(); i++) {
                float angle = -((float) i / runes.size()) * MathHelper.TAU;

                var centerPos = new Vec3d(baseX + (MathHelper.sin(MathHelper.TAU - angle) * ringGuess.radius) * 16, baseY + (-MathHelper.cos(MathHelper.TAU - angle) * ringGuess.radius) * 16, 0);

                int finalI = i;
                ButtonWidget widget = new TexturedButtonWidget(MathHelper.floor(centerPos.x - 7), MathHelper.floor(centerPos.y - 7), 14, 15, new ButtonTextures(new Identifier("recipe_book/page_forward"), new Identifier("recipe_book/page_forward_highlighted")),
                        b -> {
                            if (target.isCorrect)
                                return;
                            var cRune = runes.get(finalI);
                            var nextID = (Elements.getElement(cRune).number + 1) % Elements.getElementCount();
                            var nextRune = Elements.getElement(nextID);
                            runes.set(finalI, nextRune.id);

                            target.checkIfCorrect();
                            screen.syncNbt();
                        },
                        Text.empty()
                );

                screen.addSelectableChild(widget);
            }
        }
    }

    @Override
    public void render() {

        var target = getTarget();
        RenderingUtilities.setupNormal(0, 0, 0);

        RenderingUtilities.pushMat();

        //Render label
        {
            RenderingUtilities.pushMat();
            RenderingUtilities.setupColor(0xFF000000);

            RenderingUtilities.translateMatrix(PAGE_WIDTH / 2.0f, 10, 0);
            RenderingUtilities.drawTextCentered(target.recipeOutputStack.getItem().getName(), false);
            RenderingUtilities.popMat();
        }

        RenderingUtilities.translateMatrix(PAGE_WIDTH / 2.0f, PAGE_HEIGHT / 2.0f, 0);

        //Render item.
        {
            RenderingUtilities.pushMat();
            //if (RenderingUtilities.SPACE == RenderingUtilities.RenderingSpace.GUI)
            RenderingUtilities.getMatStack().multiplyPositionMatrix(new Matrix4f().scale(1, -1, 0.0001f));
            RenderingUtilities.scaleMatrix(16.0f, 16.0f, 16.0f);
            RenderingUtilities.translateMatrix(0, 0, 0.21f);

            RenderingUtilities.renderItem(target.recipeOutputStack, ModelTransformationMode.GUI, null);

            RenderingUtilities.popMat();
        }

        RenderingUtilities.scaleMatrix(16.0f, -16.0f, 16.0f);
        RenderingUtilities.rotateMatrix(MathHelper.HALF_PI, 0, 0);

        float globalAngle = (float) TheWorkClient.getTime() / 10.0f;
        if (!target.isCorrect)
            globalAngle = 0;

        for (RecipePage.RingGuess ringGuess : target.circleGuess) {
            var runes = ringGuess.runeGuesses;
            float circumference = ringGuess.radius * MathHelper.TAU;
            float widthNode = (0.5f / circumference) * MathHelper.TAU;

            int unitsPerSegment = MathHelper.ceil((circumference / runes.size()) * 1.5f);

            for (int i = 0; i < runes.size(); i++) {
                var element = Elements.getElement(runes.get(i));
                float angle = (((float) i / runes.size()) * MathHelper.TAU) + globalAngle;
                float angleNext = (((float) (i + 1) / runes.size()) * MathHelper.TAU) + globalAngle;

                RenderingUtilities.setupColor(0xFFFFFFFF);

                RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutoutNoCull(new Identifier(TheWorkMod.ModID, "textures/other/alchemy_circle.png")));
                AlchemyCircleRenderer.drawCircleSegment(ringGuess.radius, -(angle + widthNode + MathHelper.PI), -(angleNext - widthNode + MathHelper.PI), unitsPerSegment);

                RenderingUtilities.pushMat();
                RenderingUtilities.rotateMatrix(0, -angle, 0);
                RenderingUtilities.translateMatrix(0, 0, -ringGuess.radius);
                RenderingUtilities.rotateMatrix(0, angle, 0);

                if (element != null)
                    RenderingUtilities.setupColor(element.color);

                AlchemyCircleRenderer.drawSidedCircleAndRune(0.5f, 6, element == Elements.NONE ? TheWorkRunes.NULL : element.id, 0);

                RenderingUtilities.popMat();
            }
        }

        RenderingUtilities.popMat();
    }
}
