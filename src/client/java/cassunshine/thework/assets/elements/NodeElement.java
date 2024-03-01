package cassunshine.thework.assets.elements;

import cassunshine.thework.alchemy.runes.TheWorkRunes;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public class NodeElement extends JournalLayoutElement {
    public int sideCount;
    public Identifier runeId;

    public static NodeElement fromJson(JsonObject object) {
        var element = new NodeElement();
        apply(element, object);

        if (object.has("contents")) {
            JsonObject contentObject = object.get("contents").getAsJsonObject();

            element.sideCount = contentObject.has("sides") ? contentObject.get("sides").getAsInt() : 4;
            element.runeId = contentObject.has("rune") ? new Identifier(contentObject.get("rune").getAsString()) : TheWorkRunes.NULL;
        }

        return element;
    }
}