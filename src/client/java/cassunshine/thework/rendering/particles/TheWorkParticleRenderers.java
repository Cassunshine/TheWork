package cassunshine.thework.rendering.particles;

import cassunshine.thework.particles.TheWorkParticles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.Nullable;

public class TheWorkParticleRenderers {

    public static void initialize() {
        ParticleFactoryRegistry.getInstance().register(TheWorkParticles.RADIAL_ELEMENT, p -> new BasicParticleFactory<>(p, RadialParticle::new));
        ParticleFactoryRegistry.getInstance().register(TheWorkParticles.LINK_ELEMENT, p -> new BasicParticleFactory<>(p, PathParticle::new));
    }


    private interface ParticleGenerator<T> {
        Particle generate(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
    }

    private static class BasicParticleFactory<T extends ParticleEffect> implements ParticleFactory<T> {

        private final SpriteProvider spriteProvider;
        private final ParticleGenerator<T> generator;

        public BasicParticleFactory(SpriteProvider spriteProvider, ParticleGenerator<T> supplier) {
            this.spriteProvider = spriteProvider;
            this.generator = supplier;
        }

        @Nullable
        @Override
        public Particle createParticle(T parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            var particle = generator.generate(world, x, y, z, velocityX, velocityY, velocityZ);
            if (particle instanceof SpriteBillboardParticle billboardParticle)
                billboardParticle.setSprite(spriteProvider);
            return particle;
        }
    }
}
