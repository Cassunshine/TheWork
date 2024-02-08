package cassunshine.thework.elements;

import cassunshine.thework.TheWorkMod;
import net.minecraft.util.Identifier;

public class Element {
    public Identifier id;
    public int color;

    public Element(Identifier id, int color) {
        this.id = id;
        this.color = color;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
