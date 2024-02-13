package cassunshine.thework.rendering.model;

import cassunshine.thework.TheWorkMod;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.util.Identifier;

public class TheWorkModelPlugin implements ModelLoadingPlugin {

    public static final ChemistrySetModel CHEMISTRY_SET_MODEL = new ChemistrySetModel();
    public static final Identifier CHEMISTRY_SET_MODEL_ID = new Identifier(TheWorkMod.ModID, "block/chemistry_set");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        /*pluginContext.modifyModelOnLoad().register((original, context) -> {
            if (context.id().equals(CHEMISTRY_SET_MODEL_ID))
                return CHEMISTRY_SET_MODEL;
            return original;
        });*/
    }
}
