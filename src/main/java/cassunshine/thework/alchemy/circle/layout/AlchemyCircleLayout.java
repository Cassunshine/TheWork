package cassunshine.thework.alchemy.circle.layout;

import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.elements.Elements;
import cassunshine.thework.utils.ShiftSorting;
import cassunshine.thework.utils.TheWorkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

/**
 * Used to determine how an alchemy circle is physically laid out, which nodes exist, and where.
 */
public class AlchemyCircleLayout {

    public final AlchemyCircle circle;

    public final String fullSignature;

    public final ArrayList<RingLayout> rings = new ArrayList<>();

    public AlchemyCircleLayout(AlchemyCircle circle, Predicate<AlchemyNode> predicate) {
        for (AlchemyRing ring : circle.rings)
            rings.add(new RingLayout(ring, predicate));

        fullSignature = TheWorkUtils.generateSignature(rings, r -> r.signature);

        this.circle = circle;
    }


    public static class RingLayout {

        /**
         * Signature of this ring individually.
         */
        public final String signature;

        /**
         * Nodes within this ring, already sorted using ShiftSorting.
         */
        public final ArrayList<AlchemyNode> nodes = new ArrayList<>();


        public RingLayout(AlchemyRing ring, Predicate<AlchemyNode> predicate) {

            for (AlchemyNode node : ring.nodes) {
                if (!predicate.test(node))
                    continue;

                nodes.add(node);
            }

            int offset = ShiftSorting.findShiftValue(nodes, n -> {
                var element = Elements.getElement(n.rune);
                return element.number;
            });
            Collections.rotate(nodes, offset);

            signature = TheWorkUtils.generateSignature(nodes, n -> n.rune.toString());
        }
    }
}
