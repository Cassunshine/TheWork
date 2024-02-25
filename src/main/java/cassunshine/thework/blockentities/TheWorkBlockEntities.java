package cassunshine.thework.blockentities;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.blockentities.chemistry.ChemistrySetBlockEntity;
import cassunshine.thework.blockentities.chemistry.DistilleryBlockEntity;
import cassunshine.thework.blockentities.jar.AlchemyJarBlockEntity;
import cassunshine.thework.blocks.TheWorkBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TheWorkBlockEntities {

    public static final BlockEntityType<AlchemyCircleBlockEntity> ALCHEMY_CIRCLE_TYPE;
    public static final BlockEntityType<DistilleryBlockEntity> DISTILLERY_TYPE;


    public static final BlockEntityType<AlchemyJarBlockEntity> ALCHEMY_JAR_TYPE;


    static {
        ALCHEMY_CIRCLE_TYPE = FabricBlockEntityTypeBuilder.create(AlchemyCircleBlockEntity::new, TheWorkBlocks.ALCHEMY_CIRCLE_BLOCK).build();
        DISTILLERY_TYPE = FabricBlockEntityTypeBuilder.create(DistilleryBlockEntity::new, TheWorkBlocks.DISTILLERY_BLOCK).build();

        ALCHEMY_JAR_TYPE = FabricBlockEntityTypeBuilder.create(AlchemyJarBlockEntity::new, TheWorkBlocks.ALCHEMY_JAR_BLOCK).build();
    }

    public static void initialize() {
        registerBlockEntity(ALCHEMY_CIRCLE_TYPE, "alchemy_circle");
        registerBlockEntity(DISTILLERY_TYPE, "distillery");

        registerBlockEntity(ALCHEMY_JAR_TYPE, "alchemy_jar");
    }


    public static void registerBlockEntity(BlockEntityType<?> type, String name) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(TheWorkMod.ModID, name), type);
    }

}
