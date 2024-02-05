package cassunshine.thework.elements;

import cassunshine.thework.TheWorkMod;
import net.minecraft.util.Identifier;

public class Element {
    public Identifier id;
    public int color;

    public Element(String name, int color) {
        id = new Identifier(TheWorkMod.ModID, name);

        TheWorkElements.ELEMENT_HASH_MAP.put(id, this);
    }
}
