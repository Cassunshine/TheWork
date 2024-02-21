package cassunshine.thework.rendering.particles;

import cassunshine.thework.alchemy.circle.path.AlchemyPath;
import cassunshine.thework.particles.TheWorkParticles;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

public class PathParticle extends SpriteBillboardParticle {

    private final Vec3d startPos;
    private final Vec3d endPos;

    private final double travelDistance;

    private float progress = 0;

    protected PathParticle(ClientWorld clientWorld, double x, double y, double z, double dstX, double dstY, double dstZ) {
        super(clientWorld, x, y, z);
        this.maxAge = Integer.MAX_VALUE;
        this.setColor(ColorHelper.Argb.getRed(TheWorkParticles.particleColor) / 255.0f, ColorHelper.Argb.getGreen(TheWorkParticles.particleColor) / 255.0f, ColorHelper.Argb.getBlue(TheWorkParticles.particleColor) / 255.0f);
        this.scale = 0.05f;

        this.startPos = new Vec3d(x, y, z);
        this.endPos = new Vec3d(dstX, dstY, dstZ);

        this.travelDistance = startPos.distanceTo(endPos);

    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();

        if (progress >= travelDistance)
            markDead();

        //Move along path...
        progress += AlchemyPath.TRAVEL_SPEED;

        var progressFrac = progress / travelDistance;
        Vec3d newPos = startPos.lerp(endPos, progressFrac);

        x = newPos.x;
        y = newPos.y;
        z = newPos.z;
    }
}
