package cassunshine.thework.network.events.effects;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpawnBoltEvent extends TheWorkNetworkEvent {

    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "spawn_bolt");

    public Vec3d startPos;
    public Vec3d endPos;

    public Element element;

    public int quality = 0;

    public SpawnBoltEvent() {
        super(IDENTIFIER);
    }

    public SpawnBoltEvent(Vec3d startPos, Vec3d endPos, Element element, int quality) {
        this();
        this.startPos = startPos;
        this.endPos = endPos;
        this.element = element;
        this.quality = quality;
    }


    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeVec3d(startPos);
        buf.writeVec3d(endPos);
        buf.writeInt(element.number);

        buf.writeInt(quality);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        startPos = buf.readVec3d();
        endPos = buf.readVec3d();
        element = Elements.getElement(buf.readInt());
        quality = buf.readInt();
    }

    @Override
    public void applyToWorld(World world) {
        super.applyToWorld(world);

        if (!world.isClient)
            return;
    }
}
