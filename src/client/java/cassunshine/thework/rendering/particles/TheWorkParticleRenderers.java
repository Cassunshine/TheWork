package cassunshine.thework.rendering.particles;

import cassunshine.thework.particles.TheWorkParticles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.FlameParticle;

public class TheWorkParticleRenderers {

    public static void initialize() {


        ParticleFactoryRegistry.getInstance().register(TheWorkParticles.RADIAL_ELEMENT, RadialParticle.Factory::new);
    }
}
