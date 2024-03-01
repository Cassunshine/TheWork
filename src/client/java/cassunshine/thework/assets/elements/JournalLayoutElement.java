package cassunshine.thework.assets.elements;

import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;
import com.google.gson.JsonObject;

import java.util.Locale;

public class JournalLayoutElement {

    public int x;
    public int y;
    public int width;
    public int height;

    public Alignment xAlignment;
    public Alignment yAlignment;

    public static void apply(JournalLayoutElement element, JsonObject object) {
        element.x = object.has("x") ? object.get("x").getAsInt() : 0;
        element.y = object.has("y") ? object.get("y").getAsInt() : 0;
        element.width = object.has("width") ? object.get("width").getAsInt() : 0;
        element.height = object.has("height") ? object.get("height").getAsInt() : 0;

        element.xAlignment = object.has("xAlignment") ? Alignment.valueOf(object.get("xAlignment").getAsString().toUpperCase(Locale.ROOT)) : Alignment.START;
        element.yAlignment = object.has("yAlignment") ? Alignment.valueOf(object.get("yAlignment").getAsString().toUpperCase(Locale.ROOT)) : Alignment.START;

        int realX = 0;
        int realY = 0;
        int realWidth = 0;
        int realHeight = 0;

        switch (element.xAlignment) {
            case START -> realX = element.x;
            case CENTER -> realX = (AlchemistNotebookPage.PAGE_WIDTH / 2) + element.x;
            case END -> realX = AlchemistNotebookPage.PAGE_WIDTH - element.x;
        }
        switch (element.yAlignment) {
            case START -> realY = element.y;
            case CENTER -> realY = (AlchemistNotebookPage.PAGE_HEIGHT / 2) + element.y;
            case END -> realY = AlchemistNotebookPage.PAGE_HEIGHT - element.y;
        }

        switch (element.width) {
            case -1 -> realWidth = AlchemistNotebookPage.PAGE_WIDTH - (realX * 2);
            default -> realWidth = element.width;
        }

        switch (element.height) {
            case -1 -> realHeight = AlchemistNotebookPage.PAGE_HEIGHT - (realY * 2);
            default -> realHeight = element.height;
        }

        element.x = realX;
        element.y = realY;
        element.width = realWidth;
        element.height = realHeight;
    }

    public enum Alignment {
        START,
        CENTER,
        END
    }
}
