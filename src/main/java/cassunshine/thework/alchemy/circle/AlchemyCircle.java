package cassunshine.thework.alchemy.circle;

import cassunshine.thework.alchemy.circle.events.circle.ActivateToggleEvent;
import cassunshine.thework.alchemy.circle.events.circle.AddRingEvent;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.path.AlchemyLink;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;

public class AlchemyCircle implements AlchemyCircleComponent {

    /**
     * Reference to the block entity this circle belongs to.
     */
    public final AlchemyCircleBlockEntity blockEntity;

    /**
     * Holds all rings in this circle.
     */
    public final ArrayList<AlchemyRing> rings = new ArrayList<>();

    public final ArrayList<AlchemyLink> links = new ArrayList<>();

    public AlchemyCircleConstructLayout constructNodeLayout;

    /**
     * Stores if the circle is currently in an active alchemical reaction.
     */
    public boolean isActive = false;

    public AlchemyCircle(AlchemyCircleBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public void regenerateLayouts() {
        constructNodeLayout = new AlchemyCircleConstructLayout(this);
    }

    /**
     * Adds a new ring at the specified radius to the circle.
     */
    public void addRing(float radius) {

        //Search for existing ring of similar size. If one exists, remove it, instead.
        for (int i = 0; i < rings.size(); i++) {
            var radiusDifference = MathHelper.abs(radius - rings.get(i).radius);

            if (radiusDifference < 1) {
                var ring = rings.remove(i);
                blockEntity.regenerateInteractionPoints();

                //Clear all links involving this node.
                for (AlchemyNode node : ring.nodes) {
                    for (int j = links.size() - 1; j >= 0; j--) {
                        var link = links.get(j);

                        if (link.sourceNode == node || link.destinationNode == node)
                            links.remove(j);
                    }
                }

                return;
            }
        }

        //Add ring, if none nearby exist.
        var ring = new AlchemyRing(this);
        ring.setRadius(radius);

        rings.add(ring);
        sortRings();

        blockEntity.regenerateInteractionPoints();
    }


    private void sortRings() {
        rings.sort(Comparator.comparingDouble(a -> a.radius));

        for (int i = 0; i < rings.size(); i++)
            rings.get(i).index = i;

        regenerateLayouts();
    }


    public void addLink(AlchemyLink newLink) {

        for (int i = links.size() - 1; i >= 0; i--) {
            var link = links.get(i);

            if (link.sourceNode == newLink.sourceNode && link.destinationNode == newLink.destinationNode) {
                links.remove(i);
                return;
            }
        }

        links.add(newLink);
    }

    public void updateLinkLengths() {
        for (AlchemyLink link : links) {
            link.updateLength();
        }
    }

    @Override
    public void activate() {
        isActive = true;

        for (AlchemyRing ring : rings)
            ring.activate();
    }

    @Override
    public void activeTick() {
        for (AlchemyRing ring : rings)
            ring.activeTick();

        if (constructNodeLayout.recipe != null)
            constructNodeLayout.tryProduceItem();
    }

    @Override
    public void deactivate() {
        isActive = false;

        for (AlchemyRing ring : rings)
            ring.deactivate();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList ringsList = new NbtList();
        NbtList linkList = new NbtList();

        //Write lists to NBT
        for (int i = 0; i < rings.size(); i++)
            ringsList.add(rings.get(i).writeNbt(new NbtCompound()));
        for (int i = 0; i < links.size(); i++)
            linkList.add(links.get(i).writeNbt(new NbtCompound()));

        nbt.put("rings", ringsList);
        nbt.put("links", linkList);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList ringsList = nbt.getList("rings", NbtElement.COMPOUND_TYPE);
        NbtList linkList = nbt.getList("links", NbtElement.COMPOUND_TYPE);

        //Re-create rings.
        rings.clear();
        for (int i = 0; i < ringsList.size(); i++) {
            var newRing = new AlchemyRing(this);
            newRing.readNbt(ringsList.getCompound(i));

            if(newRing.radius != 0)
                rings.add(newRing);
        }

        links.clear();
        for (int i = 0; i < linkList.size(); i++) {
            var newLink = new AlchemyLink(this);
            newLink.readNbt(linkList.getCompound(i));

            links.add(newLink);
        }

        sortRings();
    }

    @Override
    public TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context) {

        //You can't use chalk on a circle while it's active.
        if (isActive) {
            return TheWorkNetworkEvents.NONE;
        }

        //Try to interact with chalk, first, adding a new ring.
        if (context.getBlockPos().equals(blockEntity.getPos())) {
            var tag = context.getStack().getOrCreateNbt();
            if (!tag.contains("last_circle", NbtElement.LONG_TYPE)) {
                tag.putLong("last_circle", blockEntity.getPos().asLong());

                return TheWorkNetworkEvents.SUCCESS;
            } else {
                var pos = BlockPos.fromLong(tag.getLong("last_circle"));
                var dist = MathHelper.sqrt((float) pos.withY(0).getSquaredDistance(context.getBlockPos().withY(0)));

                //If clicking the same block, do nothing.
                if (dist <= 0) return TheWorkNetworkEvents.NONE;

                tag.remove("last_circle");

                //Otherwise, add a new ring.
                return new AddRingEvent(dist, blockEntity.circle);
            }
        }

        //Try to do chalk interaction on rings
        for (AlchemyRing ring : rings) {
            var event = ring.generateChalkEvent(context);
            if (event != TheWorkNetworkEvents.NONE) return event;
        }

        return TheWorkNetworkEvents.NONE;
    }

    @Override
    public TheWorkNetworkEvent generateInteractEvent(ItemUsageContext context) {

        if (context.getBlockPos().equals(blockEntity.getPos()))
            return new ActivateToggleEvent(this);

        //Try to do normal interaction on the rings.
        for (AlchemyRing ring : rings) {
            var event = ring.generateInteractEvent(context);
            if (event != TheWorkNetworkEvents.NONE) return event;
        }


        return TheWorkNetworkEvents.NONE;
    }

    @Override
    public void regenerateInteractionPoints(AlchemyCircleBlockEntity blockEntity) {
        for (AlchemyRing ring : rings)
            ring.regenerateInteractionPoints(blockEntity);
    }
}
