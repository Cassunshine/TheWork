package cassunshine.thework.rendering.model;

import cassunshine.thework.TheWorkMod;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class TheWorkModelPlugin implements ModelLoadingPlugin {

    private static AlchemyJarModel jarModel;
    public static final Identifier ALCHEMICAL_JAR_BLOCK_MODEL = new ModelIdentifier(TheWorkMod.ModID, "block/alchemy_jar", "");
    public static final Identifier ALCHEMICAL_JAR_ITEM_MODEL = new ModelIdentifier(TheWorkMod.ModID, "alchemy_jar", "inventory");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            if (context.id().equals(ALCHEMICAL_JAR_ITEM_MODEL)) {
                return new AlchemyJarModel(original);
            }

            if(context.id().getNamespace().equals(TheWorkMod.ModID)){
                //TheWorkMod.LOGGER.error(context.id().toString());
            }

            return original;
        });
    }
}
