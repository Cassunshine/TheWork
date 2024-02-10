package cassunshine.thework.alchemy.circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.events.circle.AddRingEvent;
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

    /**
     * Determines if the circle is constructed with rings going inward or outward.
     */
    public boolean isOutward = false;

    /**
     * Stores if the circle is currently in an active alchemical reaction.
     */
    public boolean isActive = false;

    public AlchemyCircle(AlchemyCircleBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    /**
     * Adds a new ring at the specified radius to the circle.
     */
    public void addRing(float radius) {

        //Search for existing ring of similar size. If one exists, remove it, instead.
        for (int i = 0; i < rings.size(); i++) {
            var radiusDifference = MathHelper.abs(radius - rings.get(i).radius);

            if (radiusDifference < 1) {
                rings.remove(i);
                blockEntity.regenerateInteractionPoints();
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
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList ringsList = new NbtList();

        //Write lists to NBT
        for (int i = 0; i < rings.size(); i++)
            ringsList.add(rings.get(i).writeNbt(new NbtCompound()));

        nbt.put("rings", ringsList);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList ringsList = nbt.getList("rings", NbtElement.COMPOUND_TYPE);

        //Re-create rings.
        rings.clear();
        for (int i = 0; i < ringsList.size(); i++) {
            var newRing = new AlchemyRing(this);
            newRing.readNbt(ringsList.getCompound(i));

            rings.add(newRing);
        }

        sortRings();
    }

    @Override
    public TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context) {

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
