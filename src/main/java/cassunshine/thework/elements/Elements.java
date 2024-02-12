package cassunshine.thework.elements;

import cassunshine.thework.TheWorkMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class Elements {


    private static int lastNumber = 0;
    private static final ArrayList<Element> ELEMENTS_BY_NUMBER = new ArrayList<>();


    public static final HashMap<Identifier, Element> ELEMENT_HASH_MAP = new HashMap<>();

    public static final Element NONE = register("none", ColorHelper.Argb.getArgb(0, 0, 0, 0));

    public static final Element IGNIS = register("ignis", ColorHelper.Argb.getArgb(255, 245, 149, 66));
    public static final Element TERRA = register("terra", ColorHelper.Argb.getArgb(255, 102, 74, 49));
    public static final Element AQUA = register("aqua", ColorHelper.Argb.getArgb(255, 20, 194, 252));
    public static final Element VENTUS = register("ventus", ColorHelper.Argb.getArgb(255, 217, 252, 238));

    public static void initialize() {
    }

    private static Element register(String name, int color) {
        Identifier id = new Identifier(TheWorkMod.ModID, name);
        Element element = new Element(id, color, lastNumber++);

        ELEMENTS_BY_NUMBER.add(element);
        ELEMENT_HASH_MAP.put(id, element);
        return element;
    }

    public static int getElementCount() {
        return lastNumber;
    }

    public static Element getElement(int number) {
        return ELEMENTS_BY_NUMBER.get(number);
    }

    public static Element getElement(Identifier identifier) {
        return ELEMENT_HASH_MAP.getOrDefault(identifier, NONE);
    }
}
