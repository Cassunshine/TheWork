package cassunshine.thework.particles;

import cassunshine.thework.TheWorkMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TheWorkParticles {

    public static final DefaultParticleType RADIAL_ELEMENT = register(new Identifier(TheWorkMod.ModID, "radial_element"), FabricParticleTypes.simple());

    public static void initialize() {

    }

    public static <T extends ParticleType<?>> T register(Identifier id, T particleType) {
        Registry.register(Registries.PARTICLE_TYPE, id, particleType);
        return particleType;
    }
}
