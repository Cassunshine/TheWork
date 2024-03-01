package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.client.utils.ClientJournalPage;
import cassunshine.thework.items.notebook.pages.NodePickPage;
import cassunshine.thework.items.notebook.pages.AlchemistNotebookPage;
import cassunshine.thework.items.notebook.pages.RecipePage;
import com.google.common.collect.ImmutableMap;

import java.util.function.Supplier;

public class AlchemistNotebookPageRenderers {
    private static final ImmutableMap<Class<?>, Supplier<AlchemistNotebookPageRenderer<?>>> renderers;

    static {
        ImmutableMap.Builder<Class<?>, Supplier<AlchemistNotebookPageRenderer<?>>> builder = new ImmutableMap.Builder<>();

        builder.put(NodePickPage.class, NodePageRenderer::new);
        builder.put(RecipePage.class, RecipePageRenderer::new);
        builder.put(ClientJournalPage.class, ClientJournalPageRenderer::new);


        renderers = builder.build();
    }


    public static <T extends AlchemistNotebookPage> AlchemistNotebookPageRenderer<T> getRenderer(T page) {
        if (page == null)
            return null;

        var factory = renderers.get(page.getClass());

        if (factory == null)
            return null;

        //This cast is safe tho :3
        AlchemistNotebookPageRenderer<T> value = (AlchemistNotebookPageRenderer<T>) factory.get();
        value.init(page);

        return value;
    }
}
