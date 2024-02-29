package cassunshine.thework.client.rendering.entities;

import cassunshine.thework.entities.TheWorkEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

public class TheWorkEntityRenderers {

    public static void initialize() {
        EntityRendererRegistry.register(TheWorkEntities.INTERACTION_POINT_TYPE, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(TheWorkEntities.BACKFIRE_ENTITY_TYPE, EmptyEntityRenderer::new);
    }

}
