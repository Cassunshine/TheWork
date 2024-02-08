package cassunshine.thework.rendering.particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RadialParticle extends SpriteBillboardParticle {

    public static int color = Integer.MAX_VALUE;

    private double radius;
    private double startAngle;
    private double endAngle;

    private Vec3d startPos;

    boolean direction = true;

    protected RadialParticle(ClientWorld clientWorld, double x, double y, double z, double radius, double startAngle, double endAngle) {
        super(clientWorld, x + MathHelper.sin((float) startAngle) * radius, y, z + MathHelper.cos((float) startAngle) * radius);

        this.radius = radius;
        this.startAngle = startAngle;
        this.endAngle = endAngle;

        startPos = new Vec3d(x, y, z);
        scale = 0.05f;

        double circumference = MathHelper.TAU * radius;

        prevPosX = x + MathHelper.sin((float) (startAngle - ((0.1 * MinecraftClient.getInstance().getTickDelta()) / circumference))) * radius;
        prevPosY = y;
        prevPosZ = z + MathHelper.cos((float) (startAngle - ((0.1 * MinecraftClient.getInstance().getTickDelta()) / circumference))) * radius;

        this.maxAge = Integer.MAX_VALUE;

        this.setColor(ColorHelper.Argb.getRed(color) / 255.0f, ColorHelper.Argb.getGreen(color) / 255.0f, ColorHelper.Argb.getBlue(color) / 255.0f);

        direction = startAngle < endAngle;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();

        if (direction && startAngle >= endAngle)
            this.markDead();
        if(!direction && startAngle <= endAngle)
            this.markDead();

        x = startPos.x + MathHelper.sin((float) startAngle) * radius;
        z = startPos.z + MathHelper.cos((float) startAngle) * radius;

        double circumference = MathHelper.TAU * radius;

        if(direction)
            startAngle += MathHelper.TAU * (0.1f / circumference);
        else
            startAngle -= MathHelper.TAU * (0.1f / circumference);
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double velX, double velY, double velZ) {
            RadialParticle radialParticle = new RadialParticle(clientWorld, x, y, z, velX, velY, velZ);
            radialParticle.setSprite(this.spriteProvider);
            return radialParticle;
        }
    }
}
