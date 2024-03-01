package cassunshine.thework.assets;

import cassunshine.thework.TheWorkMod;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class JournalLayouts {

    public static final Gson gson = new Gson();

    private static ImmutableMap<String, LayoutHolder> LAYOUTS = ImmutableMap.of();


    public static void loadLayouts(ResourceManager manager) {
        ImmutableMap.Builder<String, LayoutHolder> builder = new ImmutableMap.Builder<>();

        var languages = MinecraftClient.getInstance().getLanguageManager().getAllLanguages().keySet();

        for (String language : languages) {
            var files = manager.findAllResources("journal/layouts/" + language, p -> p.getPath().endsWith(".json"));

            ImmutableMap.Builder<Identifier, JournalLayout> layoutBuilder = new ImmutableMap.Builder<>();
            for (var entry : files.entrySet()) {

                try {
                    var path = entry.getKey().getPath();
                    path = path.replace("journal/layouts/" + language + "/", "").replace(".json", "");
                    var identifier = entry.getKey().withPath(path);
                    var highest = entry.getValue().get(0);
                    var reader = highest.getReader();

                    var layout = JournalLayout.fromJson(gson.fromJson(reader, JsonObject.class));

                    layoutBuilder.put(identifier, layout);
                } catch (Exception e) {
                    //Ignore
                    TheWorkMod.LOGGER.error(e.toString());
                }
            }

            var holder = layoutBuilder.build();
            if (!holder.isEmpty())
                builder.put(language, new LayoutHolder(holder));
        }

        LAYOUTS = builder.build();
    }

    public static JournalLayout getLayout(Identifier identifier) {
        var language = MinecraftClient.getInstance().getLanguageManager().getLanguage();

        var holder = LAYOUTS.get(language);
        if (holder == null)
            return null;

        return holder.get(identifier);
    }

    private record LayoutHolder(ImmutableMap<Identifier, JournalLayout> layoutsByLanguage) {
        public JournalLayout get(Identifier key) {
            return layoutsByLanguage.get(key);
        }
    }
}
