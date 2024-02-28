package cassunshine.thework.client.gui.ingame.notebook;

import cassunshine.thework.client.gui.ingame.notebook.pages.AlchemistNotebookNodePage;
import cassunshine.thework.client.gui.ingame.notebook.pages.AlchemistNotebookPage;
import cassunshine.thework.client.gui.ingame.notebook.pages.RecipePage;
import cassunshine.thework.client.networking.TheWorkClientNetworking;
import cassunshine.thework.rendering.items.AlchemistNotebookRenderer;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class AlchemistNotebookScreen extends Screen {

    public static boolean isOpen = false;

    public final ItemStack stack;
    public final ArrayList<AlchemistNotebookPage> pages;

    public PageTurnWidget TURN_LEFT_WIDGET;
    public PageTurnWidget TURN_RIGHT_WIDGET;

    public AlchemistNotebookScreen(ItemStack stack) {
        super(Text.translatable("ui.alchemist_notebook.title"));

        this.pages = new ArrayList<>() {{
            add(new AlchemistNotebookNodePage(stack.getOrCreateNbt()));
        }};

        AlchemistNotebookPage.generateRecipePages(pages, stack.getOrCreateNbt());
        this.stack = stack;
    }


    public void nextPage() {
        var nbt = stack.getOrCreateNbt();
        var page = nbt.getInt("current_page");

        page = MathHelper.clamp(page + 1, 0, MathHelper.ceil(pages.size() / 2.0f) - 1);

        nbt.putInt("current_page", page);
        syncNbt();

        clearChildren();
        init();
    }

    public void previousPage() {
        var nbt = stack.getOrCreateNbt();
        var page = nbt.getInt("current_page");

        page = MathHelper.clamp(page - 1, 0, 9999999);

        nbt.putInt("current_page", page);
        syncNbt();

        init();
    }

    public void syncNbt() {
        var nbt = stack.getOrCreateNbt();
        TheWorkClientNetworking.updateBook(nbt);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void onDisplayed() {
        super.onDisplayed();

        isOpen = true;
    }

    @Override
    public void init() {
        clearChildren();
        super.init();

        int centerX = MathHelper.floor(width / 2.0f);
        int centerY = MathHelper.floor(height / 2.0f);


        int heightDifference = MathHelper.floor(height - AlchemistNotebookPage.PAGE_HEIGHT);

        TURN_RIGHT_WIDGET = this.addDrawableChild(new PageTurnWidget(centerX - 11 + 20 + AlchemistNotebookPage.PAGE_WIDTH, centerY, true, (button) -> nextPage(), true));
        TURN_LEFT_WIDGET = this.addDrawableChild(new PageTurnWidget(centerX - 13 - 20 - AlchemistNotebookPage.PAGE_WIDTH, centerY, false, (button) -> previousPage(), true));

        var nbt = stack.getOrCreateNbt();
        var currentPage = nbt.getInt("current_page") * 2;

        if (currentPage >= pages.size()) {
            currentPage = 0;
            nbt.putInt("current_page", 0);
            syncNbt();
        }

        try {
            AlchemistNotebookPage pageLeft = pages.get(currentPage);
            AlchemistNotebookPage pageRight = currentPage + 1 >= pages.size() ? null : pages.get(currentPage + 1);

            if (pageLeft != null)
                pageLeft.addInteractables(this, centerX - AlchemistNotebookPage.PAGE_WIDTH, heightDifference / 2);
            if (pageRight != null)
                pageRight.addInteractables(this, centerX, heightDifference / 2);
        } catch (Exception e) {
            //Ignore
        }
    }

    @Override
    public <T extends Element & Selectable> T addSelectableChild(T child) {
        return super.addSelectableChild(child);
    }

    @Override
    public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        return super.addDrawableChild(drawableElement);
    }

    @Override
    public <T extends Drawable> T addDrawable(T drawable) {
        return super.addDrawable(drawable);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, 0x39000000, 0x39000000);

        RenderingUtilities.SPACE = RenderingUtilities.RenderingSpace.GUI;

        var nbt = stack.getOrCreateNbt();

        int centerX = MathHelper.floor(width / 2.0f);
        int centerY = MathHelper.floor(height / 2.0f);

        int heightDifference = MathHelper.floor(height - AlchemistNotebookPage.PAGE_HEIGHT);

        var currentPage = nbt.getInt("current_page") * 2;

        if (currentPage >= pages.size()) {
            currentPage = 0;
            nbt.putInt("current_page", 0);
            syncNbt();
        }

        try {
            AlchemistNotebookPage pageLeft = pages.get(currentPage);
            AlchemistNotebookPage pageRight = currentPage + 1 >= pages.size() ? null : pages.get(currentPage + 1);

            context.drawTexture(AlchemistNotebookRenderer.BOOK_TEXTURE, centerX - AlchemistNotebookPage.PAGE_WIDTH, heightDifference / 2, AlchemistNotebookPage.PAGE_WIDTH, AlchemistNotebookPage.PAGE_HEIGHT, 0, 0, 48, 64, 144, 64);
            context.drawTexture(AlchemistNotebookRenderer.BOOK_TEXTURE, centerX, heightDifference / 2, AlchemistNotebookPage.PAGE_WIDTH, AlchemistNotebookPage.PAGE_HEIGHT, 0, 0, 48, 64, 144, 64);

            if (pageLeft != null) {
                context.getMatrices().push();
                context.getMatrices().translate(centerX - AlchemistNotebookPage.PAGE_WIDTH, heightDifference / 2.0f, 0);
                pageLeft.render();
                context.getMatrices().pop();
            }

            if (pageRight != null) {
                context.getMatrices().push();
                context.getMatrices().translate(centerX, heightDifference / 2.0f, 0);
                pageRight.render();
                context.getMatrices().pop();
            }
        } catch (Exception e) {
            //Ignore
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderInGameBackground(DrawContext context) {
    }

    @Override
    public void close() {
        super.close();

        isOpen = false;
    }
}
