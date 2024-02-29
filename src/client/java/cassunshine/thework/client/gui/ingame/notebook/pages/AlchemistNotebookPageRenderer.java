package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;

public class AlchemistNotebookPageRenderer<T> {

    public static final int PAGE_SCALE = AlchemistNotebookPage.PAGE_SCALE;
    public static final int PAGE_WIDTH = AlchemistNotebookPage.PAGE_WIDTH;
    public static final int PAGE_HEIGHT = AlchemistNotebookPage.PAGE_HEIGHT;

    private T target;

    public void init(T target) {
        this.target = target;
    }

    public T getTarget() {
        return target;
    }

    /**
     * Adds any interactable UI elements to the UI screen.
     */
    public void addInteractables(AlchemistNotebookScreen screen, int x, int y) {

    }

    /**
     * Draws everything that's a part of this notebook page.
     */
    public void render() {

    }
}
