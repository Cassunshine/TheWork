package cassunshine.thework.alchemy.circle;

import cassunshine.thework.alchemy.balance.BalanceUtils;
import cassunshine.thework.alchemy.circle.events.circle.ActivateToggleEvent;
import cassunshine.thework.alchemy.circle.events.circle.AddRingEvent;
import cassunshine.thework.alchemy.circle.events.circle.AlchemyCircleSetColorEvent;
import cassunshine.thework.alchemy.circle.layout.AlchemyCircleConstructLayout;
import cassunshine.thework.alchemy.circle.layout.AlchemyCircleResearchLayout;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.alchemy.circle.path.AlchemyLink;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.alchemy.elements.inventory.ElementInventory;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import cassunshine.thework.entities.BackfireEntity;
import cassunshine.thework.items.ChalkItem;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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
    public AlchemyCircleResearchLayout researchLayout;

    /**
     * Holds all the elements added by the circle's components when de-activating. Will spawn backfire entities to empty this.
     */
    private final ElementInventory backfireInventory = new ElementInventory();

    /**
     * Stores if the circle is currently in an active alchemical reaction.
     */
    public boolean isActive = false;

    public int color = 0xFFFFFFFF;


    int backfireCooldown = 0;
    public float circleChaos = 0;
    public float circleChaosSquare = 0;

    public AlchemyCircle(AlchemyCircleBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public void regenerateLayouts() {
        constructNodeLayout = new AlchemyCircleConstructLayout(this);
        researchLayout = new AlchemyCircleResearchLayout(this);

        circleChaos = BalanceUtils.calculateCircleChaos(this);
        circleChaosSquare = circleChaos * circleChaos;
        //TheWorkMod.LOGGER.error("CHAOS IS " + circleChaos);
    }

    /**
     * Adds a new ring at the specified radius to the circle.
     */
    public void addRing(float radius, int color) {

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

                        link.onDestroy();
                    }
                }

                ring.onDestroy();
                return;
            }
        }

        //Add ring, if none nearby exist.
        var ring = new AlchemyRing(this);
        ring.setRadius(radius);
        ring.color = color;

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

        if (newLink.sourceNode == newLink.destinationNode)
            return;

        for (int i = links.size() - 1; i >= 0; i--) {
            var link = links.get(i);

            if (link.sourceNode == newLink.sourceNode && link.destinationNode == newLink.destinationNode) {
                links.remove(i);
                link.sourceNode.link = null;
                updateLinkLengths();
                return;
            }
        }

        links.add(newLink);
        updateLinkLengths();
    }


    private void spawnActiveParticles() {
        var random = blockEntity.getWorld().random;
        if (random.nextInt() % 10 > 3)
            return;

        Vec3d basePosition = blockEntity.getPos().toCenterPos();
        float randomRadius = MathHelper.sqrt(random.nextFloat()) * (blockEntity.circle.rings.get(blockEntity.circle.rings.size() - 1).radius);
        float randomAngle = random.nextFloat() * MathHelper.TAU;

        float tangent = randomAngle + MathHelper.HALF_PI;

        basePosition = basePosition.add(MathHelper.sin(randomAngle) * randomRadius, -0.5f, MathHelper.cos(randomAngle) * randomRadius);

        blockEntity.getWorld().addParticle(ParticleTypes.ENCHANT, basePosition.x, basePosition.y + 3, basePosition.z, MathHelper.sin(tangent), -3, MathHelper.cos(tangent));
    }

    public void updateLinkLengths() {
        for (AlchemyLink link : links) {
            link.updateLength();
            link.sourceNode.link = link;
        }

        regenerateLayouts();
    }

    public void addBackfire(Element element, float amount) {
        //Ignore client-side, server will spawn backfire.
        if (blockEntity.getWorld().isClient)
            return;

        backfireInventory.put(element, amount);
    }


    public void addBackfire(ElementInventory inventory) {
        //Ignore client-side, server will spawn backfire.
        if (blockEntity.getWorld().isClient)
            return;

        inventory.transfer(backfireInventory, Float.POSITIVE_INFINITY);
    }

    public void dumpBackfireInventory() {

        var pos = blockEntity.getPos().toCenterPos();

        //Spawn backfire entities
        for (int i = 0; i < Elements.getElementCount(); i++) {
            var element = Elements.getElement(i);

            var amount = backfireInventory.get(element);

            if (amount > 0) {

                var entity = new BackfireEntity(null, blockEntity.getWorld());
                entity.setPos(pos.x, pos.y, pos.z);
                entity.element = element;
                entity.amount = amount;
                entity.minRadius = rings.get(rings.size() - 1).radius;
                entity.maxRadius = Math.max(entity.minRadius + 15, entity.minRadius * 1.5f); //idk if this will ever even happen but /shrug

                blockEntity.getWorld().spawnEntity(entity);
            }
        }

        backfireInventory.clear();
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void activate() {
        isActive = true;

        regenerateLayouts();

        for (AlchemyRing ring : rings)
            ring.activate();

        backfireCooldown = 20;

        researchLayout.activate();
    }

    @Override
    public void activeTick() {
        for (AlchemyRing ring : rings)
            ring.activeTick();

        if (constructNodeLayout.recipe != null)
            constructNodeLayout.tryProduceItem();

        spawnActiveParticles();

        backfireCooldown--;

        if (backfireCooldown == 0) {
            dumpBackfireInventory();
            backfireCooldown = 20;
        }

    }

    @Override
    public void deactivate() {
        if (!isActive)
            return;

        isActive = false;

        for (AlchemyRing ring : rings)
            ring.deactivate();

        dumpBackfireInventory();
    }

    @Override
    public void onDestroy() {
        for (AlchemyRing ring : rings)
            ring.onDestroy();
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
        nbt.putInt("color", color);

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

            if (newRing.radius != 0)
                rings.add(newRing);
        }

        links.clear();
        for (int i = 0; i < linkList.size(); i++) {
            var newLink = new AlchemyLink(this);
            newLink.readNbt(linkList.getCompound(i));

            links.add(newLink);
        }

        sortRings();
        updateLinkLengths();

        color = nbt.getInt("color");
    }

    @Override
    public TheWorkNetworkEvent generateChalkEvent(ItemUsageContext context) {

        //You can't use chalk on a circle while it's active.
        if (isActive) {
            return TheWorkNetworkEvents.NONE;
        }

        //Try to interact with chalk, first, adding a new ring.
        if (context.getBlockPos().equals(blockEntity.getPos())) {

            if (context.getStack().getItem() instanceof ChalkItem cI && color != cI.color)
                AlchemyCircleBlockEntity.sendCircleEvent(blockEntity, new AlchemyCircleSetColorEvent(cI.color, this));

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

                //This will always be true but java casting thingy, so.
                if (context.getStack().getItem() instanceof ChalkItem cI)
                    //Otherwise, add a new ring.
                    return new AddRingEvent(dist, cI.color, blockEntity.circle);
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

        if (isActive)
            return TheWorkNetworkEvents.NONE;

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
