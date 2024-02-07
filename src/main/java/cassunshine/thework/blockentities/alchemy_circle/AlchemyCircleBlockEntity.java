package cassunshine.thework.blockentities.alchemy_circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blockentities.alchemy_circle.layout.AlchemyCircleLayout;
import cassunshine.thework.blockentities.alchemy_circle.nodes.types.AlchemyNodeTypes;
import cassunshine.thework.blockentities.alchemy_circle.rings.AlchemyRing;
import cassunshine.thework.entities.InteractionPointEntity;
import cassunshine.thework.entities.TheWorkEntities;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;

public class AlchemyCircleBlockEntity extends BlockEntity implements AlchemyCircleComponent {

    /**
     * List of each ring, sorted by smallest-first.
     */
    public final ArrayList<AlchemyRing> rings = new ArrayList<>();

    /**
     * List of interaction point entities created by this alchemy circle.
     */
    private final ArrayList<InteractionPointEntity> interactionEntities = new ArrayList<>();

    /**
     * Refers to the bottom-middle of the block the alchemy circle lies on.
     * Considered the 'real' position of the circle.
     */
    public final Vec3d fullPosition;

    /**
     * True if the alchemy circle will pass elements outwards at each node, or inwards.
     */
    public boolean isOutward = false;

    /**
     * Determines if this circle is active, i.e. currently part of a reaction.
     */
    public boolean isActive = false;

    /**
     * Layout for all construction circles.
     */
    public AlchemyCircleLayout constructLayout;

    public float animationRotation = 0;
    private int startup = 0;

    public AlchemyCircleBlockEntity(BlockPos pos, BlockState state) {
        super(TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE, pos, state);

        fullPosition = new Vec3d(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
    }

    /**
     * Adds a new ring to the alchemy circle with the given radius, or removes a previous ring.
     * <p>
     * Utility function used to make chalk's code cleaner.
     */
    public void addRing(float radius) {
        //Try to remove a ring first.
        for (int i = 0; i < rings.size(); i++) {
            float difference = MathHelper.abs(radius - rings.get(i).radius);

            if (difference < 0.5f) {
                rings.remove(i);

                if (rings.isEmpty()) world.setBlockState(pos, Blocks.AIR.getDefaultState());
                return;
            }
        }

        AlchemyRing ring = new AlchemyRing(this);
        ring.setRadius(radius);

        //Add ring and then sort rings by radius.
        rings.add(ring);
        sortRings();
    }

    private void sortRings() {
        rings.sort(Comparator.comparingDouble(a -> a.radius));

        int lastIndex = isOutward ? rings.size() - 1 : 0;

        for (int i = 0; i < rings.size(); i++)
            rings.get(i).hasNextRing = i != lastIndex;

        updateLayouts();
    }

    /**
     * Adds an interaction point at a specified position, then returns it.
     */
    public Entity addInteractionPoint(Vec3d position) {
        if (world == null || world.isClient) return null;

        InteractionPointEntity interactionEntity = new InteractionPointEntity(TheWorkEntities.INTERACTION_POINT_TYPE, getWorld());
        interactionEntity.setPosition(position.add(0, 0, 0));

        interactionEntities.add(interactionEntity);
        world.spawnEntity(interactionEntity);

        interactionEntity.setCircle(this);

        return interactionEntity;
    }

    /**
     * Removes a previously created interaction point from the world.
     */
    public void removeInteractionPoint(Entity entity) {
        interactionEntities.remove(entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AlchemyCircleBlockEntity blockEntity) {
        //Only act on active block entities.
        if (!blockEntity.isActive) return;

        blockEntity.operate();
    }

    private void updateLayouts() {
        constructLayout = new AlchemyCircleLayout(AlchemyNodeTypes.CONSTRUCT, this);
    }

    private void attemptCraftItem() {
        //We are constructing an item, so check against recipe.
        if (!constructLayout.rings.isEmpty()) {
            var recipe = constructLayout.recipe();

            boolean shouldCraft = true;

            for (int i = 0; i < recipe.inputs().length && shouldCraft; i++) {
                var recipeRing = recipe.inputs()[i];
                var inputRing = constructLayout.rings.get(i);

                for (int j = 0; j < recipeRing.entries().length && shouldCraft; j++) {
                    var recipeNode = recipeRing.entries()[j];
                    var inputNode = inputRing.get(j);

                    shouldCraft = inputNode.inventory.has(recipeNode.element(), recipeNode.amount());
                }
            }

            if (shouldCraft) {
                for (int i = 0; i < recipe.inputs().length; i++) {
                    var recipeRing = recipe.inputs()[i];
                    var inputRing = constructLayout.rings.get(i);

                    for (int j = 0; j < recipeRing.entries().length; j++) {
                        var recipeNode = recipeRing.entries()[j];
                        var inputNode = inputRing.get(j);

                        inputNode.inventory.take(recipeNode.element(), recipeNode.amount());
                    }
                }

                for (var entry : recipe.outputs().object2IntEntrySet()) {
                    var itemID = entry.getKey();
                    var count = entry.getIntValue();

                    if (!Registries.ITEM.containsId(itemID))
                        continue;

                    var item = Registries.ITEM.get(itemID);

                    while (count > 0) {
                        int taken = Math.min(64, count);
                        count -= taken;

                        var stack = new ItemStack(item, taken);
                        var entity = new ItemEntity(getWorld(), fullPosition.x, fullPosition.y, fullPosition.z, stack);
                        getWorld().spawnEntity(entity);
                    }
                }
            }
        }
    }

    @Override
    public boolean validityCheck() {
        //Can't activate an active ring.
        if (isActive) return false;

        for (AlchemyRing ring : rings)
            if (!ring.validityCheck())
                return false;

        return true;
    }

    @Override
    public void activate() {
        isActive = true;
        startup = 20;

        for (AlchemyRing ring : rings)
            ring.activate();
    }

    @Override
    public void operate() {
        if (startup > 0) {
            startup--;
            return;
        }

        for (AlchemyRing ring : rings)
            ring.operate();

        attemptCraftItem();

        markDirty();
    }

    @Override
    public void stop() {
        for (AlchemyRing ring : rings)
            ring.stop();

        isActive = false;
    }

    @Override
    public boolean handleInteraction(ItemUsageContext context) {
        for (AlchemyRing ring : rings)
            if (ring.handleInteraction(context)) {
                markDirty();

                updateLayouts();
                return true;
            }

        return false;
    }

    @Override
    public void regenerateInteractionPoints() {
        for (InteractionPointEntity entity : interactionEntities)
            entity.remove(Entity.RemovalReason.DISCARDED);
        interactionEntities.clear();

        for (AlchemyRing ring : rings)
            ring.regenerateInteractionPoints();
    }


    @Override
    public void markRemoved() {
        super.markRemoved();

        AlchemyCircles.removeCircle(this);

        for (InteractionPointEntity entity : interactionEntities)
            entity.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        AlchemyCircles.addCircle(this);
        TheWorkMod.schedule(this::regenerateInteractionPoints);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("outward", isOutward);

        NbtList ringsList = new NbtList();
        nbt.put("rings", ringsList);

        for (AlchemyRing ring : rings) {
            NbtCompound ringCompound = new NbtCompound();

            ring.writeNbt(ringCompound);
            ringsList.add(ringCompound);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        isOutward = nbt.getBoolean("outward");

        NbtList ringList = nbt.getList("rings", NbtElement.COMPOUND_TYPE);

        for (NbtElement element : ringList) {
            if (!(element instanceof NbtCompound ringCompound)) continue;

            AlchemyRing ring = new AlchemyRing(this);
            ring.readNbt(ringCompound);

            rings.add(ring);
        }

        sortRings();
    }
}
