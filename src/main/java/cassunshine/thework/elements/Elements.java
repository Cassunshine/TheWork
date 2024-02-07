package cassunshine.thework.elements;

import cassunshine.thework.TheWorkMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.HashMap;

public class Elements {

    public static final HashMap<Identifier, Element> ELEMENT_HASH_MAP = new HashMap<>();

    public static final Element NONE = register("none", ColorHelper.Argb.getArgb(0, 0, 0, 0));

    public static final Element IGNIS = register("ignis", ColorHelper.Argb.getArgb(255, 255, 255, 255));
    public static final Element TERRA = register("terra", ColorHelper.Argb.getArgb(255, 255, 255, 255));
    public static final Element AQUA = register("aqua", ColorHelper.Argb.getArgb(255, 255, 255, 255));
    public static final Element VENTUS = register("ventus", ColorHelper.Argb.getArgb(255, 255, 255, 255));

    public static void initialize() {

    }

    private static Element register(String name, int color) {
        Identifier id = new Identifier(TheWorkMod.ModID, name);
        Element element = new Element(id, color);

        ELEMENT_HASH_MAP.put(id, element);
        return element;
    }

    public static Element getElement(Identifier identifier) {
        return ELEMENT_HASH_MAP.getOrDefault(identifier, NONE);
    }
}
