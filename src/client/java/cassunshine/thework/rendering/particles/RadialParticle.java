package cassunshine.thework.rendering.particles;

import cassunshine.thework.alchemy.circle.path.AlchemyPath;
import cassunshine.thework.particles.TheWorkParticles;
import cassunshine.thework.utils.TheWorkUtils;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


//TODO - Tidy :(
public class RadialParticle extends SpriteBillboardParticle {

    private float radius;
    private float startAngle;
    private float endAngle;

    private float progress = 0;

    private Vec3d startPos;

    boolean direction;

    protected RadialParticle(ClientWorld clientWorld, double x, double y, double z, double radius, double startAngle, double endAngle) {
        super(clientWorld, x + MathHelper.sin((float) startAngle) * radius, y, z + MathHelper.cos((float) startAngle) * radius);

        this.radius = (float) radius - (1 / 32.0f);
        this.startAngle = (float) startAngle;
        this.endAngle = (float) endAngle;

        direction = startAngle < endAngle;

        startPos = new Vec3d(x, y, z);

        var angle = getAngle(progress);
        prevPosX = x + MathHelper.sin(angle) * this.radius;
        prevPosY = y;
        prevPosZ = z + MathHelper.cos(angle) * this.radius;

        this.maxAge = Integer.MAX_VALUE;
        this.setColor(ColorHelper.Argb.getRed(TheWorkParticles.particleColor) / 255.0f, ColorHelper.Argb.getGreen(TheWorkParticles.particleColor) / 255.0f, ColorHelper.Argb.getBlue(TheWorkParticles.particleColor) / 255.0f);
        this.scale = 0.05f;

    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    private float getAngle(float progress) {
        float pathLength = (radius * MathHelper.TAU) * (TheWorkUtils.angleBetweenRadians(startAngle, endAngle) / MathHelper.TAU);
        return TheWorkUtils.lerpRadians(progress / pathLength, startAngle, endAngle);
    }

    @Override
    public void tick() {
        super.tick();

        float pathLength = (radius * MathHelper.TAU) * (TheWorkUtils.angleBetweenRadians(startAngle, endAngle) / MathHelper.TAU);
        if (progress > pathLength)
            markDead();

        //Move along path...
        progress += AlchemyPath.TRAVEL_SPEED;

        float currentAngle = TheWorkUtils.lerpRadians(progress / pathLength, startAngle, endAngle);

        x = startPos.x + MathHelper.sin(currentAngle) * radius;
        z = startPos.z + MathHelper.cos(currentAngle) * radius;
    }
}
