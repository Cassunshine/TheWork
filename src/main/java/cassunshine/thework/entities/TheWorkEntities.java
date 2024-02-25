package cassunshine.thework.entities;

import cassunshine.thework.TheWorkMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TheWorkEntities {
    public static EntityType<InteractionPointEntity> INTERACTION_POINT_TYPE = register("interaction_point", FabricEntityTypeBuilder.create().entityFactory(InteractionPointEntity::new).trackRangeBlocks(MathHelper.ceil(PlayerEntity.getReachDistance(true))).build());
    public static EntityType<BackfireEntity> BACKFIRE_ENTITY_TYPE = register("backfire_entity", FabricEntityTypeBuilder.create().entityFactory(BackfireEntity::new).trackRangeChunks(3).build());

    public static void initialize() {

    }

    public static <T extends Entity> EntityType<T> register(String name, EntityType<T> type) {
        Registry.register(Registries.ENTITY_TYPE, new Identifier(TheWorkMod.ModID, name), type);
        return type;
    }
}
