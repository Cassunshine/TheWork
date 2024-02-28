package cassunshine.thework.rendering.items;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.client.gui.ingame.notebook.pages.AlchemistNotebookNodePage;
import cassunshine.thework.client.gui.ingame.notebook.pages.AlchemistNotebookPage;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.client.gui.ingame.notebook.pages.RecipePage;
import cassunshine.thework.rendering.util.RenderingUtilities;
import cassunshine.thework.utils.TheWorkUtils;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class AlchemistNotebookRenderer {
    public static final Identifier BOOK_TEXTURE = new Identifier(TheWorkMod.ModID, "textures/item/alchemist_notebook.png");

    public static VertexConsumerProvider provider;
    public static MatrixStack stack;

    public static final float TEXTURE_SIZE = 32;

    public static void renderItem(ItemStack stack, ModelTransformationMode modelTransformationMode, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {

        AlchemistNotebookRenderer.provider = vertexConsumerProvider;
        AlchemistNotebookRenderer.stack = matrixStack;

        RenderingUtilities.setupStack(matrixStack);
        RenderingUtilities.setupConsumers(vertexConsumerProvider);
        RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutout(BOOK_TEXTURE));
        RenderingUtilities.setupLightOverlay(light, overlay);

        RenderingUtilities.setupColor(0xFFFFFFFF);

        RenderingUtilities.pushMat();

        float bookAngle = 0;
        boolean useNormal = true;
        boolean drawPages = true;
        boolean drawUI = false;

        var screenPos = new Vec3d(0, 1, 0);


        switch (modelTransformationMode) {
            case FIRST_PERSON_RIGHT_HAND: {
                if (AlchemistNotebookScreen.isOpen)
                    return;

                bookAngle = 0.9f;
                drawUI = true;

                RenderingUtilities.translateMatrix(0.4f, 0.5f, 0.75f);
                RenderingUtilities.scaleMatrix(0.3f, 0.3f, 0.3f);
                RenderingUtilities.rotateMatrix(0, -0.4f, 0);
                RenderingUtilities.SPACE = RenderingUtilities.RenderingSpace.WORLD;
                break;
            }
            case FIRST_PERSON_LEFT_HAND: {
                if (AlchemistNotebookScreen.isOpen)
                    return;

                bookAngle = 0.9f;
                drawUI = true;

                RenderingUtilities.translateMatrix(0.525f, 0.5f, 0.75f);
                RenderingUtilities.scaleMatrix(0.3f, 0.3f, 0.3f);
                RenderingUtilities.rotateMatrix(0, 0.4f, 0);
                RenderingUtilities.SPACE = RenderingUtilities.RenderingSpace.WORLD;
                break;
            }
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND: {
                bookAngle = 0.9f;

                RenderingUtilities.translateMatrix(0.5f, 0.48f, 0.55f);
                RenderingUtilities.scaleMatrix(0.5f, 0.5f, 0.5f);
                RenderingUtilities.SPACE = RenderingUtilities.RenderingSpace.WORLD;
                break;
            }
            case GUI: {
                bookAngle = 0.1f;
                useNormal = false;
                drawPages = false;

                RenderingUtilities.setupNormal(0, 1, 0);

                RenderingUtilities.scaleMatrix(0.7f, 0.7f, 0.7f);

                RenderingUtilities.translateMatrix(0.6f, 0.2f, 0);
                RenderingUtilities.rotateMatrix(0, MathHelper.HALF_PI, 0);
                RenderingUtilities.setupLightOverlay(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, overlay);

                RenderingUtilities.rotateMatrix(0.4f, 0, 0);
                RenderingUtilities.translateMatrix(0, 0, -0.5f);
                RenderingUtilities.SPACE = RenderingUtilities.RenderingSpace.WORLD;

                break;
            }
            case GROUND: {
                bookAngle = 0.1f;
                RenderingUtilities.translateMatrix(0.5f, 0.3f, 0.375f);
                RenderingUtilities.scaleMatrix(0.4f, 0.4f, 0.4f);

                RenderingUtilities.rotateMatrix(0.4f, 0, 0);
                RenderingUtilities.translateMatrix(0, 0, -0.1f);
                RenderingUtilities.SPACE = RenderingUtilities.RenderingSpace.WORLD;

                drawPages = false;

                break;
            }
        }


        var nbt = stack.getOrCreateNbt();
        var currentPage = nbt.getInt("current_page");
        ArrayList<AlchemistNotebookPage> pages = null;

        if (drawPages) {
            pages = new ArrayList<>() {{
                add(new AlchemistNotebookNodePage(nbt));
            }};

            AlchemistNotebookPage.generateRecipePages(pages, nbt);
        }

        renderBook(bookAngle, useNormal, drawPages, pages, currentPage);

        RenderingUtilities.popMat();
    }

    public static void renderBook(float bookAngle, boolean useNormals, boolean drawPages, ArrayList<AlchemistNotebookPage> pages, int currentPage) {
        bookAngle = TheWorkUtils.lerpRadians(bookAngle, 0, MathHelper.HALF_PI);

        {
            RenderingUtilities.pushMat();
            RenderingUtilities.rotateMatrix(0, bookAngle, 0);

            if (useNormals)
                RenderingUtilities.setupNormal(1, 0, 0);

            RenderingUtilities.saneVertex(0, 1, 0, 1 / 3.0f, 0);
            RenderingUtilities.saneVertex(0, 1, 0.75f, 2 / 3.0f, 0);
            RenderingUtilities.saneVertex(0, 0, 0.75f, 2 / 3.0f, 1);
            RenderingUtilities.saneVertex(0, 0, 0, 1 / 3.0f, 1);

            if (useNormals)
                RenderingUtilities.setupNormal(-1, 0, 0);

            RenderingUtilities.saneVertex(0, 0, 0, 1 / 3.0f, 1);
            RenderingUtilities.saneVertex(0, 0, 0.75f, 2 / 3.0f, 1);
            RenderingUtilities.saneVertex(0, 1, 0.75f, 2 / 3.0f, 0);
            RenderingUtilities.saneVertex(0, 1, 0, 1 / 3.0f, 0);

            RenderingUtilities.popMat();
        }

        {
            RenderingUtilities.pushMat();
            RenderingUtilities.rotateMatrix(0, -bookAngle, 0);

            if (useNormals)
                RenderingUtilities.setupNormal(-1, 0, 0);

            RenderingUtilities.saneVertex(0, 0, 0, 2 / 3.0f, 1);
            RenderingUtilities.saneVertex(0, 0, 0.75f, 3 / 3.0f, 1);
            RenderingUtilities.saneVertex(0, 1, 0.75f, 3 / 3.0f, 0);
            RenderingUtilities.saneVertex(0, 1, 0, 2 / 3.0f, 0);

            if (useNormals)
                RenderingUtilities.setupNormal(1, 0, 0);

            RenderingUtilities.saneVertex(0, 1, 0, 1 / 3.0f, 1);
            RenderingUtilities.saneVertex(0, 1, 0.75f, 2 / 3.0f, 1);
            RenderingUtilities.saneVertex(0, 0, 0.75f, 2 / 3.0f, 0);
            RenderingUtilities.saneVertex(0, 0, 0, 1 / 3.0f, 0);

            RenderingUtilities.popMat();
        }

        if (drawPages) {
            int decorativePages = 3;
            float decPageFrac = 0.02f;

            float angleFrac = 0.01f;

            for (int i = 0; i < decorativePages; i++) {
                float leftFrac = angleFrac;
                float rightFrac = angleFrac + decPageFrac;

                float leftAngle = MathHelper.lerp(leftFrac, -bookAngle, bookAngle);
                float rightAngle = MathHelper.lerp(rightFrac, -bookAngle, bookAngle);

                drawPage(useNormals, leftAngle, rightAngle, null, null);

                angleFrac = rightFrac;
            }

            {
                float leftFrac = angleFrac + decPageFrac;
                float rightFrac = 1 - (decPageFrac * (decorativePages + 1));

                float leftAngle = MathHelper.lerp(leftFrac, -bookAngle, bookAngle);
                float rightAngle = MathHelper.lerp(rightFrac + 0.01f, -bookAngle, bookAngle);

                var pIndex = currentPage * 2;

                var leftPage = pages.get(pIndex);
                var rightPage = (pIndex) + 1 >= pages.size() ? null : pages.get(pIndex + 1);

                drawPage(useNormals, leftAngle, rightAngle, leftPage, rightPage);

                angleFrac = rightFrac;
            }

            for (int i = 0; i < 3; i++) {
                float leftFrac = angleFrac;
                float rightFrac = angleFrac + 0.02f;

                float leftAngle = MathHelper.lerp(leftFrac, -bookAngle, bookAngle);
                float rightAngle = MathHelper.lerp(rightFrac + 0.01f, -bookAngle, bookAngle);

                drawPage(useNormals, leftAngle, rightAngle, null, null);

                angleFrac = rightFrac;
            }
        }
    }


    private static void drawPage(boolean useNormals, float leftAngle, float rightAngle, AlchemistNotebookPage left, AlchemistNotebookPage right) {

        RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutout(BOOK_TEXTURE));

        //Left page
        {
            if (useNormals)
                RenderingUtilities.setupNormal(1, 0, 0);

            RenderingUtilities.pushMat();
            RenderingUtilities.rotateMatrix(0, leftAngle, 0);

            RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutout(BOOK_TEXTURE));
            RenderingUtilities.saneVertex(0, 1, 0, 0, 1);
            RenderingUtilities.saneVertex(0, 1, 0.75f, 1 / 3.0f, 1);
            RenderingUtilities.saneVertex(0, 0, 0.75f, 1 / 3.0f, 0);
            RenderingUtilities.saneVertex(0, 0, 0, 0, 0);

            RenderingUtilities.popMat();
            RenderingUtilities.pushMat();

            if (left != null) {
                RenderingUtilities.rotateMatrix(0, MathHelper.HALF_PI + leftAngle, 0);
                RenderingUtilities.scaleMatrix(1 / (float) AlchemistNotebookPage.PAGE_HEIGHT, 1 / (float) AlchemistNotebookPage.PAGE_HEIGHT, 1 / (float) AlchemistNotebookPage.PAGE_HEIGHT);
                RenderingUtilities.translateMatrix(-AlchemistNotebookPage.PAGE_WIDTH, AlchemistNotebookPage.PAGE_HEIGHT, 1f);
                RenderingUtilities.scaleMatrix(1, -1, 1);
                left.render();
            }

            RenderingUtilities.popMat();
        }
        RenderingUtilities.setupColor(0xFFFFFFF);

        //Right page
        {
            if (useNormals)
                RenderingUtilities.setupNormal(-1, 0, 0);

            RenderingUtilities.pushMat();
            RenderingUtilities.rotateMatrix(0, rightAngle, 0);

            RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutout(BOOK_TEXTURE));
            RenderingUtilities.saneVertex(0, 0, 0, 0, 0);
            RenderingUtilities.saneVertex(0, 0, 0.75f, 1 / 3.0f, 0);
            RenderingUtilities.saneVertex(0, 1, 0.75f, 1 / 3.0f, 1);
            RenderingUtilities.saneVertex(0, 1, 0, 0, 1);

            RenderingUtilities.popMat();
            RenderingUtilities.pushMat();


            if (right != null) {
                RenderingUtilities.rotateMatrix(0, -MathHelper.HALF_PI + rightAngle, 0);
                RenderingUtilities.scaleMatrix(1 / (float) AlchemistNotebookPage.PAGE_HEIGHT, 1 / (float) AlchemistNotebookPage.PAGE_HEIGHT, 1 / (float) AlchemistNotebookPage.PAGE_HEIGHT);
                RenderingUtilities.translateMatrix(0, AlchemistNotebookPage.PAGE_HEIGHT, 1f);
                RenderingUtilities.scaleMatrix(1, -1, 1);
                right.render();
            }

            RenderingUtilities.popMat();
        }
        RenderingUtilities.setupColor(0xFFFFFFF);
    }
}
