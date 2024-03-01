package cassunshine.thework.assets;

import cassunshine.thework.assets.elements.IconElement;
import cassunshine.thework.assets.elements.JournalLayoutElement;
import cassunshine.thework.assets.elements.TextElement;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * Holds the client-side layout of journal pages, based on language.
 */
public class JournalLayout {

    public static final ImmutableMap<String, Function<JsonObject, JournalLayoutElement>> ELEMENT_FACTORIES;

    public final ImmutableList<JournalLayoutPage> pages;

    private JournalLayout(ImmutableList<JournalLayoutPage> pages) {
        this.pages = pages;
    }

    static {
        var builder = new ImmutableMap.Builder<String, Function<JsonObject, JournalLayoutElement>>();

        builder.put("icon", IconElement::fromJson);
        builder.put("text", TextElement::fromJson);

        ELEMENT_FACTORIES = builder.build();
    }

    public static JournalLayout fromJson(JsonObject jsonObject) {

        ImmutableList.Builder<JournalLayoutPage> pageBuilder = new ImmutableList.Builder<>();

        if (jsonObject.has("pages")) {
            var pageList = jsonObject.get("pages").getAsJsonArray();

            for (JsonElement jsonElement : pageList) {
                var pageObject = jsonElement.getAsJsonObject();
                if (pageObject == null)
                    continue;

                ImmutableList.Builder<JournalLayoutElement> elementBuilder = new ImmutableList.Builder<>();
                ImmutableList.Builder<Identifier> discoveryBuilder = new ImmutableList.Builder<>();

                if (pageObject.has("elements")) {
                    JsonArray elementsArray = pageObject.get("elements").getAsJsonArray();
                    for (JsonElement element : elementsArray) {
                        if (!(element instanceof JsonObject obj))
                            continue;

                        var type = obj.get("type");
                        if (type == null)
                            continue;

                        var factory = ELEMENT_FACTORIES.get(type.getAsString());
                        if (factory == null)
                            continue;

                        var result = factory.apply(obj);
                        elementBuilder.add(result);
                    }
                }

                if (pageObject.has("discoveries")) {
                    JsonArray discoveriesArray = pageObject.get("discoveries").getAsJsonArray();
                    for (JsonElement element : discoveriesArray) {
                        var string = element.getAsString();
                        if (string == null)
                            continue;

                        discoveryBuilder.add(new Identifier(string));
                    }
                }

                pageBuilder.add(new JournalLayoutPage(elementBuilder.build(), discoveryBuilder.build()));
            }
        }

        return new JournalLayout(pageBuilder.build());
    }

    public static class JournalLayoutPage {
        public final ImmutableList<JournalLayoutElement> elements;
        public final ImmutableList<Identifier> discoveries;

        public JournalLayoutPage(ImmutableList<JournalLayoutElement> elements, ImmutableList<Identifier> discoveries) {
            this.elements = elements;
            this.discoveries = discoveries;
        }
    }
}
