package cassunshine.thework.alchemy.elements;

import net.minecraft.util.Identifier;

public class Element {
    public Identifier id;
    public int color;

    public int number;

    public Element(Identifier id, int color, int score) {
        this.id = id;
        this.color = color;
        this.number = score;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
