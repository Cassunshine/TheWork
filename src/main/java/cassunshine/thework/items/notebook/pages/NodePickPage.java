package cassunshine.thework.items.notebook.pages;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.runes.TheWorkRunes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class NodePickPage extends AlchemistNotebookPage {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "node_select");

    public int sides = 8;
    public Identifier runeID = TheWorkRunes.NULL;

    public NodePickPage() {
        super(IDENTIFIER);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("sides", sides);
        nbt.putString("rune_id", runeID.toString());

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        sides = nbt.getInt("sides");
        runeID = new Identifier(nbt.getString("rune_id"));
    }
}
