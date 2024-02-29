package cassunshine.thework.client.gui.ingame.notebook;

import cassunshine.thework.client.gui.ingame.notebook.drawables.SectionWidget;
import cassunshine.thework.client.gui.ingame.notebook.drawables.TextureDrawer;
import cassunshine.thework.client.gui.ingame.notebook.pages.AlchemistNotebookPageRenderer;
import cassunshine.thework.client.gui.ingame.notebook.pages.AlchemistNotebookPageRenderers;
import cassunshine.thework.client.networking.TheWorkClientNetworking;
import cassunshine.thework.items.notebook.NotebookData;
import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;
import cassunshine.thework.items.notebook.sections.AlchemistNotebookSection;
import cassunshine.thework.client.rendering.items.AlchemistNotebookRenderer;
import cassunshine.thework.client.rendering.util.RenderingUtilities;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class AlchemistNotebookScreen extends Screen {

    public static boolean isOpen = false;

    public final ItemStack stack;

    public final NotebookData data = new NotebookData();

    public AlchemistNotebookSection currentSection;
    public AlchemistNotebookPageRenderer leftPage;
    public AlchemistNotebookPageRenderer rightPage;

    public PageTurnWidget TURN_LEFT_WIDGET;
    public PageTurnWidget TURN_RIGHT_WIDGET;

    public TextureDrawer LEFT_PAGE_DRAWER;
    public TextureDrawer RIGHT_PAGE_DRAWER;

    public final ArrayList<SectionWidget> sectionWidgets = new ArrayList<>();

    public AlchemistNotebookScreen(ItemStack stack) {
        super(Text.translatable("ui.alchemist_notebook.title"));

        data.readNbt(stack.getOrCreateNbt());
        data.currentSection = MathHelper.clamp(data.currentSection, 0, data.sections.size());
        currentSection = data.getCurrentSection();

        data.currentPage = MathHelper.clamp(data.currentPage, 0, currentSection.pages.size());

        this.stack = stack;

        for (AlchemistNotebookSection section : data.sections)
            sectionWidgets.add(new SectionWidget(this, section, 8 * AlchemistNotebookPage.PAGE_SCALE, 10 * AlchemistNotebookPage.PAGE_SCALE));

        updatePages();
    }

    public void updatePages() {
        var pages = currentSection.getPages(data.currentPage);

        leftPage = AlchemistNotebookPageRenderers.getRenderer(pages.getLeft());
        rightPage = AlchemistNotebookPageRenderers.getRenderer(pages.getRight());

        clearChildren();
        init();
    }

    public void nextPage() {
        var maxPage = MathHelper.ceil(currentSection.pages.size() / 2.0f);
        var newIndex = data.currentPage + 2;

        if (newIndex > maxPage)
            return;

        data.currentPage = newIndex;
        updatePages();

        syncNbt();
    }

    public void previousPage() {
        var newIndex = data.currentPage - 2;

        if (newIndex < 0)
            return;

        data.currentPage = newIndex;
        updatePages();

        syncNbt();
    }

    public void setCurrentSection(AlchemistNotebookSection target) {

        for (int i = 0; i < data.sections.size(); i++) {
            if (target == data.sections.get(i)) {

                data.currentSection = i;
                data.currentPage = 0;
                currentSection = target;

                updatePages();
                syncNbt();
            }
        }
    }

    public void syncNbt() {
        var nbt = data.writeNbt(new NbtCompound());

        stack.setNbt(nbt);
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
        int topGap = MathHelper.floor(height - AlchemistNotebookPage.PAGE_HEIGHT);

        TURN_RIGHT_WIDGET = this.addDrawableChild(new PageTurnWidget(centerX - 11 + 20 + AlchemistNotebookPage.PAGE_WIDTH, centerY, true, (button) -> nextPage(), true));
        TURN_LEFT_WIDGET = this.addDrawableChild(new PageTurnWidget(centerX - 13 - 20 - AlchemistNotebookPage.PAGE_WIDTH, centerY, false, (button) -> previousPage(), true));

        int x = centerX - AlchemistNotebookPage.PAGE_WIDTH;
        for (SectionWidget widget : sectionWidgets) {
            addDrawableChild(widget);

            widget.setX(x);
            widget.setY((topGap / 2) - 24);

            x += widget.getWidth() + 2;
        }

        LEFT_PAGE_DRAWER = this.addDrawable(new TextureDrawer(AlchemistNotebookRenderer.BOOK_TEXTURE,
                centerX - AlchemistNotebookPage.PAGE_WIDTH, topGap / 2,
                AlchemistNotebookPage.PAGE_WIDTH, AlchemistNotebookPage.PAGE_HEIGHT,
                0, 0,
                48, 64,
                144, 64
        ));

        RIGHT_PAGE_DRAWER = this.addDrawable(new TextureDrawer(AlchemistNotebookRenderer.BOOK_TEXTURE,
                centerX, topGap / 2,
                AlchemistNotebookPage.PAGE_WIDTH, AlchemistNotebookPage.PAGE_HEIGHT,
                0, 0,
                48, 64,
                144, 64
        ));

        if (leftPage != null)
            leftPage.addInteractables(this, centerX - AlchemistNotebookPage.PAGE_WIDTH, topGap / 2);
        if (rightPage != null)
            rightPage.addInteractables(this, centerX, topGap / 2);
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

        int centerX = MathHelper.floor(width / 2.0f);
        int centerY = MathHelper.floor(height / 2.0f);
        int topGap = MathHelper.floor(height - AlchemistNotebookPage.PAGE_HEIGHT);

        int heightDifference = MathHelper.floor(height - AlchemistNotebookPage.PAGE_HEIGHT);

        super.render(context, mouseX, mouseY, delta);

        try {
            if (leftPage != null) {
                context.getMatrices().push();
                context.getMatrices().translate(centerX - AlchemistNotebookPage.PAGE_WIDTH, topGap / 2.0f, 0);
                leftPage.render();
                context.getMatrices().pop();
            }

            if (rightPage != null) {
                context.getMatrices().push();
                context.getMatrices().translate(centerX, topGap / 2.0f, 0);
                rightPage.render();
                context.getMatrices().pop();
            }
        } catch (Exception e) {
            //Ignore
        }
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
