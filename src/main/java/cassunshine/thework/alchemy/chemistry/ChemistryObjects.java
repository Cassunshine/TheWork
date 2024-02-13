package cassunshine.thework.alchemy.chemistry;

import cassunshine.thework.TheWorkMod;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.function.Supplier;

public class ChemistryObjects {
    private static final HashMap<Identifier, Supplier<ChemistryObject>> FACTORIES = new HashMap<>();

    public static void initialize() {
        register(FurnaceBurnerChemistryObject.IDENTIFIER, FurnaceBurnerChemistryObject::new);
    }

    public static void register(Identifier id, Supplier<ChemistryObject> factory) {
        FACTORIES.put(id, factory);
    }

    public static ChemistryObject generateObject(Identifier identifier) {
        var factory = FACTORIES.get(identifier);

        if (factory == null)
            return null;

        return factory.get();
    }
}
