package cassunshine.thework.alchemy.circle.path;

import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.particles.TheWorkParticles;
import net.minecraft.util.math.MathHelper;

public class AlchemyRingPath extends AlchemyPath {

    public final AlchemyRing ring;
    public final int index;

    /**
     * The angle that the path starts at on the ring.
     */
    public float startAngle = 0;
    /**
     * The angle that the path ends at on the ring.
     */
    public float endAngle = 0;

    public AlchemyRingPath(AlchemyRing ring, int index, float length) {
        super(length);

        this.ring = ring;
        this.index = index;
    }

    @Override
    public void spawnParticle(Element element, float progress) {
        var angle = MathHelper.lerp(progress / length, startAngle, endAngle);

        var position = ring.circle.blockEntity.flatPosition.add(0, ring.circle.blockEntity.getPos().getY() + 0.15f, 0);
        TheWorkParticles.particleColor = element.color;
        ring.circle.blockEntity.getWorld().addParticle(TheWorkParticles.RADIAL_ELEMENT, position.x, position.y, position.z, ring.radius, angle, endAngle);
    }

    @Override
    public void deactivate() {
        //Throw all elements from this ring into the backfire.
        for (ElementInstance element : elements)
            ring.circle.addBackfire(element.element, 1);

        super.deactivate();
    }
}
