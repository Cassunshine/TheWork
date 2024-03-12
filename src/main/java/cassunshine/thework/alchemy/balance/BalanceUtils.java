package cassunshine.thework.alchemy.balance;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import net.minecraft.util.math.Vec3d;

/**
 * Higher balance = more likely for elements to release themselves as they travel along the circle
 */
public class BalanceUtils {

    public static float calculateCircleChaos(AlchemyCircle circle) {
        var rings = circle.rings;

        float ringTotalChaos = 0;
        boolean lastRingDirection = false;

        for (int i = 0; i < rings.size(); i++) {
            AlchemyRing ring = rings.get(i);

            //Rings REALLY like to be alternating clockwise and counterclockwise.
            if (i != 0 && ring.isClockwise == lastRingDirection)
                ringTotalChaos += 0.2f;

            lastRingDirection = ring.isClockwise;
            ringTotalChaos += calculateRingChaos(ring);
        }

        if (ringTotalChaos < 0.001f)
            return 0;

        return ringTotalChaos;
    }

    public static float calculateRingChaos(AlchemyRing ring) {

        //Calculate chaos caused by unbalanced nodes.
        float nodeChaos = 0;
        {
            var nodeTotalPosition = Vec3d.ZERO;
            var nodeCount = 0;


            for (AlchemyNode node : ring.nodes) {
                if (node.nodeType != AlchemyNodeTypes.NONE) {
                    nodeTotalPosition = nodeTotalPosition.add(node.getPositionRelative());
                    nodeCount++;
                }

                if (node.link != null) {
                    nodeTotalPosition.add(node.getPositionRelative().lerp(node.link.destinationNode.getPositionRelative(), 0.5f));
                    nodeCount++;

                    //Nodes REALLY don't like to be connected to nodes on the same ring. It's possible, but they don't like it.
                    if (node.ring == node.link.destinationNode.ring)
                        nodeChaos += 0.2f;
                }
            }

            if (nodeCount != 0) {
                var averageNodePosition = nodeTotalPosition.multiply(1 / (float) nodeCount);

                //Increase node chaos by checking how far the center of all nodes is from the center of the circle.
                nodeChaos += (float) (averageNodePosition.length() / 10);
            }
        }

        return nodeChaos;
    }

}
