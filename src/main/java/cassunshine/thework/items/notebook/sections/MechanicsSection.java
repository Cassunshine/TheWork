package cassunshine.thework.items.notebook.sections;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.TheWorkItems;
import cassunshine.thework.items.notebook.NotebookData;
import cassunshine.thework.items.notebook.pages.NodePickPage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class MechanicsSection extends AlchemistNotebookSection {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "mechanics");

    public final NodePickPage nodePage = new NodePickPage();

    public MechanicsSection(NotebookData data) {
        super(data, IDENTIFIER, new ItemStack(TheWorkItems.WHITE_CHALK));

        pages.add(nodePage);
    }
}
