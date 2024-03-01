package cassunshine.thework.alchemy.circle.layout;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.AlchemyCircle;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.alchemy.elements.ElementPacket;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.data.recipes.TheWorkRecipes;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import cassunshine.thework.network.events.bookevents.DiscoverMechanicEvent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Collections;

public class AlchemyCircleResearchLayout extends AlchemyCircleLayout {

    public AlchemyCircleResearchLayout(AlchemyCircle circle) {
        super(circle, p -> p.nodeType == AlchemyNodeTypes.RESEARCH);
    }


    public void activate() {
        if (rings.isEmpty())
            return;

        var world = circle.blockEntity.getWorld();
        var be = circle.blockEntity;

        var entityList = world.getEntitiesByClass(ItemEntity.class, new Box(circle.blockEntity.getPos()), p -> true);
        if (entityList == null || entityList.isEmpty())
            return;

        //Pick random item entity.
        var itemEntity = entityList.get(world.random.nextInt(entityList.size()));
        var stack = itemEntity.getStack();

        //Try to get the recipe that's used to construct the given item.
        var constructRecipe = TheWorkRecipes.getConstruction(stack.getItem());
        if (constructRecipe == null)
            return;

        //Remove 1 item from the stack.
        stack.decrement(1);

        TheWorkNetworkEvents.sendBookLearnEvent(circle.blockEntity.getPos(), circle.blockEntity.getWorld(), new DiscoverMechanicEvent(new Identifier(TheWorkMod.ModID, "5_side_node")));

        ArrayList<ElementPacket> packets = new ArrayList<>();
        for (int i = 0; i < constructRecipe.inputRings.length; i++) {
            var ringRecipe = constructRecipe.inputRings[i];
            var ringLayout = rings.get(i);

            //Add all the packets from this ring into a list.
            Collections.addAll(packets, ringRecipe);

            //Iterate over each research node
            for (AlchemyNode node : ringLayout.nodes) {
                //Get the element of this node, and the current packet.
                var element = Elements.getElement(node.rune);
                var currentPacket = packets.get(0);

                //If the element of this node is the same as the packet, consume the packet.
                if (element == currentPacket.element()) {
                    node.inventory.put(currentPacket.element(), currentPacket.amount());
                    packets.remove(0);
                }
            }

            //Anything left in the list, backfire it.
            for (ElementPacket packet : packets)
                circle.addBackfire(packet.element(), packet.amount());


            //Clear the remaining packets now that we backfired them.
            packets.clear();
        }
    }
}
