package cassunshine.thework.client.utils;

import cassunshine.thework.assets.JournalLayouts;
import cassunshine.thework.items.notebook.NotebookData;
import cassunshine.thework.items.notebook.pages.journal.JournalPage;
import cassunshine.thework.items.notebook.sections.JournalSection;
import net.minecraft.util.Identifier;

public class NotebookDataUtils {

    public static void convertJournals(NotebookData data) {
        convertJournalSection(data.mechanicsSection);
        convertJournalSection(data.storySection);
    }

    private static void convertJournalSection(JournalSection section) {
        section.pages.clear();
        section.pages.addAll(section.defaultPages);

        for (int i = section.pages.size() - 1; i >= 0; i--) {
            var page = section.pages.get(i);
            if (!(page instanceof JournalPage))
                continue;

            var layout = JournalLayouts.getLayout(page.id);
            if (layout == null)
                continue;

            section.pages.remove(i);

            for (var layoutPage : layout.pages) {
                var newPage = new ClientJournalPage(layoutPage);
                section.pages.add(i, newPage);
            }
        }

        for (Identifier identifier : section.journalPages) {
            var layout = JournalLayouts.getLayout(identifier);
            if (layout == null)
                continue;

            for (var layoutPage : layout.pages) {
                var page = new ClientJournalPage(layoutPage);

                section.pages.add(page);
            }
        }
    }
}
