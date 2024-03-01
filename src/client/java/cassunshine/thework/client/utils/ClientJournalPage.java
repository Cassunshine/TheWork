package cassunshine.thework.client.utils;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.assets.JournalLayout;
import cassunshine.thework.items.notebook.pages.journal.JournalPage;
import net.minecraft.util.Identifier;

public class ClientJournalPage extends JournalPage {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "client/journal_page");

    public final JournalLayout.JournalLayoutPage layout;

    public ClientJournalPage(JournalLayout.JournalLayoutPage layout) {
        super(IDENTIFIER);
        this.layout = layout;
    }
}
