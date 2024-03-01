package cassunshine.thework.assets.elements;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class TextElement extends JournalLayoutElement {
    public ImmutableList<Text> text;

    public static TextElement fromJson(JsonObject object) {
        var element = new TextElement();
        apply(element, object);

        if (object.has("contents")) {
            ImmutableList.Builder<Text> textList = new ImmutableList.Builder<>();

            var textRenderer = MinecraftClient.getInstance().textRenderer;

            String contents = object.get("contents").getAsString();
            StringBuilder lineBuilder = new StringBuilder();
            StringBuilder wordBuilder = new StringBuilder();

            int lineWidth = 0;

            for (int i = 0; i < contents.length(); i++) {
                var c = contents.charAt(i);

                //New lines just, newline, lol
                if (c == '\n') {
                    lineBuilder.append(wordBuilder);
                    textList.add(Text.literal(lineBuilder.toString()));

                    if (!wordBuilder.isEmpty())
                        wordBuilder.delete(0, wordBuilder.length());
                    if (!lineBuilder.isEmpty())
                        lineBuilder.delete(0, lineBuilder.length());
                    lineWidth = 0;
                    continue;
                }

                //If whitespace, try to auto-wrap.
                if (Character.isWhitespace(c)) {
                    var wordWidth = textRenderer.getWidth(wordBuilder.toString());

                    //If this new word makes the line wider than the text, wrap it instead.
                    if (lineWidth + 1 + wordWidth >= element.width) {
                        textList.add(Text.literal(lineBuilder.toString()));
                        lineBuilder.delete(0, lineBuilder.length());
                    }

                    wordBuilder.append(c);
                    //Add word to line and reset word builder.
                    lineBuilder.append(wordBuilder);
                    lineWidth = textRenderer.getWidth(lineBuilder.toString());
                    wordBuilder.delete(0, wordBuilder.length());
                    continue;
                }

                //If character was anything except a whitespace or a newline, simply add it to the word.
                wordBuilder.append(c);
            }

            {
                var wordWidth = textRenderer.getWidth(wordBuilder.toString());

                //If this new word makes the line wider than the text, wrap it instead.
                if (lineWidth + 1 + wordWidth >= element.width) {
                    textList.add(Text.literal(lineBuilder.toString()));
                    lineBuilder.delete(0, lineBuilder.length());
                }

                if (!wordBuilder.isEmpty())
                    lineBuilder.append(wordBuilder.toString().trim());

                if (!lineBuilder.isEmpty())
                    textList.add(Text.literal(lineBuilder.toString().trim()));
            }

            element.text = textList.build();
        }

        return element;
    }
}
