package cassunshine.thework.alchemy.circle.events.circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.path.AlchemyLink;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import cassunshine.thework.network.events.bookevents.DiscoverMechanicEvent;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class CreateLinkEvent extends AlchemyCircleEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "circle_create_link");

    public int ringAIndex;
    public int nodeAIndex;


    public int ringBIndex;
    public int nodeBIndex;

    public CreateLinkEvent() {
        super(IDENTIFIER);
    }

    public CreateLinkEvent(AlchemyCircle circle, AlchemyNode a, AlchemyNode b) {
        super(circle, IDENTIFIER);

        ringAIndex = a.ring.index;
        nodeAIndex = a.index;

        ringBIndex = b.ring.index;
        nodeBIndex = b.index;
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeInt(ringAIndex);
        buf.writeInt(nodeAIndex);

        buf.writeInt(ringBIndex);
        buf.writeInt(nodeBIndex);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        ringAIndex = buf.readInt();
        nodeAIndex = buf.readInt();

        ringBIndex = buf.readInt();
        nodeBIndex = buf.readInt();
    }

    @Override
    public void applyToCircle(AlchemyCircle circle) {
        var nodeA = circle.rings.get(ringAIndex).getNode(nodeAIndex);
        var nodeB = circle.rings.get(ringBIndex).getNode(nodeBIndex);

        var posA = nodeA.getPositionFlat();
        var posB = nodeB.getPositionFlat();

        var distance = (float) posA.distanceTo(posB);

        //Ensure the link ALWAYS points inward.
        if (ringBIndex > ringAIndex) {
            var tmp = nodeA;
            nodeA = nodeB;
            nodeB = tmp;
        }

        var newLink = new AlchemyLink(circle);
        newLink.sourceNode = nodeA;
        newLink.destinationNode = nodeB;

        newLink.updateLength();

        circle.addLink(newLink);
    }
}
