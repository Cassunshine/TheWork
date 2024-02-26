package cassunshine.thework.client.gui.ingame.notebook;

import cassunshine.thework.client.networking.TheWorkClientNetworking;
import cassunshine.thework.rendering.items.AlchemistNotebookRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
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

        this.pages = AlchemistNotebookPage.getPages(stack);
        this.stack = stack;
    }


    public void nextPage() {
        var nbt = stack.getOrCreateNbt();
        var page = nbt.getInt("current_page");

        page = MathHelper.clamp(page + 1, 0, (pages.size() / 2));

        nbt.putInt("current_page", page);
        syncNbt();

        clearChildren();
        init();
    }

    public void previousPage() {
        var nbt = stack.getOrCreateNbt();
        var page = nbt.getInt("current_page");

        page = MathHelper.clamp(page - 1, 0, (pages.size() / 2) - 1);

        nbt.putInt("current_page", page);
        syncNbt();

        clearChildren();
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
    protected void init() {
        super.init();

        int centerX = MathHelper.floor(width / 2.0f);
        int centerY = MathHelper.floor(height / 2.0f);

        int pageHeight = MathHelper.floor(height * 0.9f);
        int pageWidth = MathHelper.floor(pageHeight * 0.5f);

        int heightDifference = MathHelper.floor(height - pageHeight);

        TURN_RIGHT_WIDGET = this.addDrawableChild(new PageTurnWidget(centerX - 11 + 20 + pageWidth, centerY, true, (button) -> nextPage(), true));
        TURN_LEFT_WIDGET = this.addDrawableChild(new PageTurnWidget(centerX - 13 - 20 - pageWidth, centerY, false, (button) -> previousPage(), true));

        var nbt = stack.getOrCreateNbt();
        var currentPage = nbt.getInt("current_page") * 2;

        AlchemistNotebookPage pageLeft = pages.get(currentPage);
        AlchemistNotebookPage pageRight = currentPage + 1 >= pages.size() ? null : pages.get(currentPage + 1);

        if (pageLeft != null)
            pageLeft.init(this, centerX - pageWidth, heightDifference / 2, pageWidth, pageHeight);
        if (pageRight != null)
            pageRight.init(this, centerX, heightDifference / 2, pageWidth, pageHeight);
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

        var nbt = stack.getOrCreateNbt();

        int centerX = MathHelper.floor(width / 2.0f);
        int centerY = MathHelper.floor(height / 2.0f);

        int pageHeight = MathHelper.floor(height * 0.9f);
        int pageWidth = MathHelper.floor(pageHeight * 0.5f);

        int heightDifference = MathHelper.floor(height - pageHeight);

        var currentPage = nbt.getInt("current_page") * 2;

        AlchemistNotebookPage pageLeft = pages.get(currentPage);
        AlchemistNotebookPage pageRight = currentPage + 1 >= pages.size() ? null : pages.get(currentPage + 1);

        context.drawTexture(AlchemistNotebookRenderer.BOOK_TEXTURE, centerX - pageWidth, heightDifference / 2, pageWidth, pageHeight, 8, 16, 8, 16, 32, 32);
        context.drawTexture(AlchemistNotebookRenderer.BOOK_TEXTURE, centerX, heightDifference / 2, pageWidth, pageHeight, 8, 16, 8, 16, 32, 32);

        if (pageLeft != null && pageLeft.drawing != null) {
            context.drawTexture(pageLeft.drawing, centerX - pageWidth, heightDifference / 2, pageWidth, pageHeight, 0, 0, 128, 256, 128, 256);
        }

        if (pageRight != null && pageRight.drawing != null) {
            context.drawTexture(pageRight.drawing, centerX, heightDifference / 2, pageWidth, pageHeight, 0, 0, 128, 256, 128, 256);
        }

        if (pageLeft != null)
            pageLeft.render(this, context, mouseX, mouseY, delta);
        if (pageRight != null)
            pageRight.render(this, context, mouseX, mouseY, delta);


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
