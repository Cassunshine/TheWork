package cassunshine.thework.blockentities;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.AlchemyCircleBlockEntity;
import cassunshine.thework.blocks.TheWorkBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TheWorkBlockEntities {

    public static final BlockEntityType<AlchemyCircleBlockEntity> ALCHEMY_CIRCLE_TYPE;

    static {
        ALCHEMY_CIRCLE_TYPE = FabricBlockEntityTypeBuilder.create(AlchemyCircleBlockEntity::new, TheWorkBlocks.ALCHEMY_CIRCLE_BLOCK).build();
    }

    public static void initialize() {
        registerBlockEntity(ALCHEMY_CIRCLE_TYPE, "alchemy_circle");
    }


    public static void registerBlockEntity(BlockEntityType<?> type, String name) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(TheWorkMod.ModID, name), type);
    }

}