package cassunshine.thework.alchemy.runes;

import cassunshine.thework.TheWorkMod;
import net.minecraft.util.Identifier;

public class TheWorkRunes {

    private static final Identifier[] RUNES_BY_ID;

    public static final Identifier NULL = new Identifier(TheWorkMod.ModID, "null");
    public static final Identifier IGNIS = new Identifier(TheWorkMod.ModID, "ignis");
    public static final Identifier TERRA = new Identifier(TheWorkMod.ModID, "terra");
    public static final Identifier AQUA = new Identifier(TheWorkMod.ModID, "aqua");
    public static final Identifier VENTUS = new Identifier(TheWorkMod.ModID, "ventus");

    public static final Identifier FERRUM = new Identifier(TheWorkMod.ModID, "ferrum");
    public static final Identifier CUPRUM = new Identifier(TheWorkMod.ModID, "cuprum");
    public static final Identifier AURUM = new Identifier(TheWorkMod.ModID, "aurum");

    public static final Identifier GEHNIUM = new Identifier(TheWorkMod.ModID, "gehnium");
    public static final Identifier FINIUM = new Identifier(TheWorkMod.ModID, "finium");

    public static final Identifier MANA = new Identifier(TheWorkMod.ModID, "mana");

    public static final Identifier SPLIT = new Identifier(TheWorkMod.ModID, "split");


    static {
        RUNES_BY_ID = new Identifier[]{
                NULL,
                IGNIS,
                TERRA,
                AQUA,
                VENTUS,
                CUPRUM,
                FERRUM,
                AURUM,
                GEHNIUM,
                FINIUM,
                MANA,
                SPLIT
        };
    }


    public static int getRuneCount() {
        return RUNES_BY_ID.length;
    }

    public static Identifier getRuneByID(int id) {
        return RUNES_BY_ID[id];
    }

    private static int getRuneNumericalId(Identifier id) {
        for (int i = 0; i < RUNES_BY_ID.length; i++)
            if (RUNES_BY_ID[i].equals(id))
                return i;
        return -1;
    }

    public static Identifier getPreviousRune(Identifier id) {
        int index = getRuneNumericalId(id);
        index = index - 1;

        if (index == -1)
            return RUNES_BY_ID[RUNES_BY_ID.length - 1];

        return RUNES_BY_ID[index];
    }

    public static Identifier getNextRune(Identifier id) {
        int index = getRuneNumericalId(id);
        index = (index + 1) % getRuneCount();

        return RUNES_BY_ID[index];
    }
}
