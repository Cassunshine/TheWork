package cassunshine.thework.client.gui.ingame.notebook.pages;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.client.gui.ingame.notebook.AlchemistNotebookScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class AlchemistNotebookPage extends Screen {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "basic");
    private static final HashMap<Identifier, Function<NbtCompound, AlchemistNotebookPage>> NBT_GENERATORS = new HashMap<>() {{
        put(IDENTIFIER, AlchemistNotebookPage::new);
    }};

    private static final HashMap<Identifier, Supplier<AlchemistNotebookPage>> GENERATORS = new HashMap<>() {{
        put(AlchemistNotebookNodePage.IDENTIFIER, AlchemistNotebookNodePage::new);
        put(LegendNotebookPage.IDENTIFIER, LegendNotebookPage::new);
        put(DeconstructTutorialPage.IDENTIFIER, DeconstructTutorialPage::new);
    }};

    public static final Identifier[] DEFAULT_PAGES = new Identifier[]{
            AlchemistNotebookNodePage.IDENTIFIER,
            LegendNotebookPage.IDENTIFIER,
            DeconstructTutorialPage.IDENTIFIER
    };

    public Identifier typeId;
    public Identifier drawing;

    public AlchemistNotebookPage(NbtCompound compound) {
        super(null);
        readNbt(compound);
    }

    public AlchemistNotebookPage(Identifier id, Identifier drawing) {
        super(null);

        this.typeId = id;
        this.drawing = drawing;
    }

    public void init(AlchemistNotebookScreen screen, int x, int y, int width, int height) {

    }


    public void render(AlchemistNotebookScreen screen, DrawContext context, int mouseX, int mouseY, float delta) {

    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("type", typeId.toString());
        if (drawing != null)
            nbt.putString("drawing", drawing.toString());

        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        typeId = new Identifier(nbt.getString("type"));
        if (nbt.contains("drawing"))
            drawing = new Identifier(nbt.getString("drawing"));
    }

    public static ArrayList<AlchemistNotebookPage> getPages(ItemStack stack) {
        var nbt = stack.getOrCreateNbt();

        var nbtList = nbt.getList("pages", NbtElement.COMPOUND_TYPE);
        var list = new ArrayList<AlchemistNotebookPage>();

        for (Identifier page : DEFAULT_PAGES) {
            var factoryResult = GENERATORS.get(page).get();
            list.add(factoryResult);
        }

        for (int i = 0; i < nbtList.size(); i++) {
            var compound = nbtList.getCompound(i);
            var type = new Identifier(compound.getString("type"));

            var factoryResult = NBT_GENERATORS.get(type).apply(compound);
            list.add(factoryResult);
        }

        return list;
    }


    public static NbtList pagesToNbt(ArrayList<AlchemistNotebookPage> pages) {
        var list = new NbtList();

        for (int i = 0; i < pages.size(); i++) {
            if (i < DEFAULT_PAGES.length)
                continue;

            AlchemistNotebookPage page = pages.get(i);
            list.add(page.writeNbt(new NbtCompound()));
        }

        return list;
    }
}
