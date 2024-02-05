package cassunshine.thework.elements;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.HashMap;

public class TheWorkElements {

    public static final HashMap<Identifier, Element> ELEMENT_HASH_MAP = new HashMap<>();

    public static Element IGNIS = new Element("ignis", ColorHelper.Argb.getArgb(255, 255, 255, 255));
    public static Element TERRA = new Element("terra", ColorHelper.Argb.getArgb(255, 255, 255, 255));
    public static Element AQUA = new Element("aqua", ColorHelper.Argb.getArgb(255, 255, 255, 255));
    public static Element VENTUS = new Element("ventus", ColorHelper.Argb.getArgb(255, 255, 255, 255));

    public static void initialize() {

    }
}
