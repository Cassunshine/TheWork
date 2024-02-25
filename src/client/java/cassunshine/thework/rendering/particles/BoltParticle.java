package cassunshine.thework.rendering.particles;

import cassunshine.thework.alchemy.elements.Element;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class BoltParticle extends SpriteBillboardParticle {

    private static final Vec3d FORWARD = new Vec3d(0, 0, 1);

    public final int SIDES = 4;

    public Vec3d[] CLOSE_POINT_CACHE = new Vec3d[SIDES];
    public Vec3d[] FAR_POINT_CACHE = new Vec3d[SIDES];


    private final Vec3d originPoint;
    private final Vec3d endPoint;

    private final Element element;

    private final ArrayList<Vec3d> geometry = new ArrayList<>();
    private final ArrayList<Vec3d> normals = new ArrayList<>();

    private int previousAge = 0;


    public BoltParticle(ClientWorld world, double x, double y, double z, double targetX, double targetY, double targetZ, Element element) {
        super(world, x, y, z, 0, 0, 0);

        this.originPoint = new Vec3d(x, y, z);
        this.endPoint = new Vec3d(targetX, targetY, targetZ);

        this.element = element;

        setSprite(TheWorkParticleRenderers.BasicParticleFactory.spriteProvider);
        setMaxAge(3);


        int numPoints = MathHelper.ceil(originPoint.distanceTo(endPoint) / 2.0f);

        Vec3d[] midPoints = new Vec3d[numPoints];
        Quaternionf[] rotations = new Quaternionf[numPoints];
        Matrix3f rotationMatrix = new Matrix3f();

        //Generate the positions themselves
        for (int i = 0; i < numPoints; i++) {
            float progressFrac = (i / (float) numPoints);
            var point = originPoint.lerp(endPoint, progressFrac);

            if(i != 0 && i != numPoints - 1)
                point = point.add((world.random.nextFloat() - 0.5f) * 1.3f, (world.random.nextFloat() - 0.5f) * 1.3f, (world.random.nextFloat() - 0.5f) * 1.3f);

            midPoints[i] = point;
        }

        //Calculate rotations for each point
        for (int i = 0; i < numPoints; i++) {
            if (i == numPoints - 1)
                rotations[i] = rotations[i - 1];
            else
                rotations[i] = lookAt(midPoints[i], midPoints[i + 1]);
        }

        //Genrate geometry for each segment
        for (int s = 0; s < numPoints - 1; s++) {
            var thisPos = midPoints[s];
            var thisRot = rotations[s];

            var nextPos = midPoints[s + 1];
            var nextRot = rotations[s + 1];

            for (int p = 0; p < SIDES; p++) {
                float angle = (p / (float) SIDES) * MathHelper.TAU;
                float nextAngle = ((p + 1) / (float) SIDES) * MathHelper.TAU;

                geometry.add(thisPos);
                normals.add(generateNormal(thisRot, nextAngle));
                geometry.add(thisPos);
                normals.add(generateNormal(thisRot, angle));

                geometry.add(nextPos);
                normals.add(generateNormal(nextRot, angle));
                geometry.add(nextPos);
                normals.add(generateNormal(nextRot, nextAngle));
            }
        }

        //TODO - Generate caps
    }

    private Vec3d generateNormal(Quaternionf rotation, float angle) {
        Vector3f vec = new Vector3f(MathHelper.sin(angle), MathHelper.cos(angle), 0);
        vec.rotate(rotation);

        return new Vec3d(vec.x, vec.y, vec.z);
    }

    public Quaternionf lookAt(Vec3d origin, Vec3d target) {
        var forward = target.subtract(origin).normalize();
        var dot = FORWARD.dotProduct(forward);

        if (Math.abs(dot + 1) < 0.0001f)
            return new Quaternionf();
        if (Math.abs(dot - 1) < 0.0001f)
            new Quaternionf();

        var rotAngle = (float) Math.acos(dot);
        var rotAxis = FORWARD.crossProduct(forward).normalize();
        var ret = new Quaternionf();

        ret.setAngleAxis(rotAngle, rotAxis.x, rotAxis.y, rotAxis.z);
        return ret;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        previousAge = age;
        super.tick();
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        var camPos = camera.getPos();
        float midU = MathHelper.lerp(0.5f, getMinU(), getMaxU());
        float midV = MathHelper.lerp(0.5f, getMinV(), getMaxV());

        var light = getBrightness(tickDelta);

        var r = ColorHelper.Argb.getRed(element.color);
        var g = ColorHelper.Argb.getGreen(element.color);
        var b = ColorHelper.Argb.getBlue(element.color);
        var a = ColorHelper.Argb.getAlpha(element.color);

        float life = MathHelper.lerp(tickDelta, previousAge, age) / (float) maxAge;
        float extrusion = MathHelper.lerp(life, 0.05f, 0);

        for (int i = 0; i < geometry.size(); i++) {
            var actual = getActual(i, extrusion);
            vertexConsumer.vertex(actual.x - camPos.x, actual.y - camPos.y, actual.z - camPos.z).texture(midU, midV).color(r, g, b, a).light(light).next();
        }
    }

    private Vec3d getActual(int index, float extrusion) {
        return geometry.get(index).add(normals.get(index).multiply(extrusion));
    }
}
