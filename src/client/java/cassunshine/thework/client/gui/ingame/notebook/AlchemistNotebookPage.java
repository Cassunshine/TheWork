package cassunshine.thework.client.gui.ingame.notebook;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.client.networking.TheWorkClientNetworking;
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

public class AlchemistNotebookPage extends Screen {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "basic");
    private static final HashMap<Identifier, Function<NbtCompound, AlchemistNotebookPage>> GENERATORS = new HashMap<>() {{
        put(IDENTIFIER, AlchemistNotebookPage::new);
        put(AlchemistNotebookNodePage.IDENTIFIER, AlchemistNotebookNodePage::new);
    }};

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

    protected void init(AlchemistNotebookScreen screen, int x, int y, int width, int height) {

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

        if (!stack.hasNbt()) {
            var nbt = stack.getOrCreateNbt();

            nbt.putInt("current_page", 0);

            var list = new ArrayList<AlchemistNotebookPage>() {{
                add(new AlchemistNotebookNodePage());
                add(new AlchemistNotebookPage(IDENTIFIER, new Identifier(TheWorkMod.ModID, "textures/pages/page-basics.png")));
                add(new AlchemistNotebookPage(IDENTIFIER, new Identifier(TheWorkMod.ModID, "textures/pages/page-runes.png")));
            }};

            nbt.put("pages", pagesToNbt(list));

            TheWorkClientNetworking.updateBook(nbt);
            return list;
        } else {
            var nbt = stack.getOrCreateNbt();

            var nbtList = nbt.getList("pages", NbtElement.COMPOUND_TYPE);
            var list = new ArrayList<AlchemistNotebookPage>();

            for (int i = 0; i < nbtList.size(); i++) {
                var compound = nbtList.getCompound(i);
                var type = new Identifier(compound.getString("type"));

                var factoryResult = GENERATORS.get(type).apply(compound);
                list.add(factoryResult);
            }

            return list;
        }
    }


    public static NbtList pagesToNbt(ArrayList<AlchemistNotebookPage> pages) {
        var list = new NbtList();

        for (AlchemistNotebookPage page : pages)
            list.add(page.writeNbt(new NbtCompound()));

        return list;
    }
}
