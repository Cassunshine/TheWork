package cassunshine.thework.alchemy.backfire;

import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.entities.BackfireEntity;
import cassunshine.thework.network.events.BlockPosEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.ArrayList;

public class BackfireEffects {

    private static final ImmutableMap<Element, ArrayList<ElementalBackfireEffect>> BACKFIRE_REGISTRY;


    static {
        var builder = new ImmutableMap.Builder<Element, ArrayList<ElementalBackfireEffect>>();

        for (int i = 1; i < Elements.getElementCount(); i++) {
            var element = Elements.getElement(i);

            builder.put(element, new ArrayList<>());
        }

        BACKFIRE_REGISTRY = builder.build();
    }

    public static void initialize() {

        register(Elements.TERRA, new PlaceBlockBackfireEffect(Elements.TERRA, Blocks.DIRT.getDefaultState()).withCost(1));
        register(Elements.TERRA, new PlaceBlockBackfireEffect(Elements.TERRA, Blocks.STONE.getDefaultState()).withCost(8));

        //Sort everything by cost.
        for (ArrayList<ElementalBackfireEffect> value : BACKFIRE_REGISTRY.values())
            value.sort((a, b) -> Float.compare(a.cost, b.cost));
    }

    private static void register(Element element, ElementalBackfireEffect effect) {
        BACKFIRE_REGISTRY.get(element).add(effect);
    }

    public static TheWorkNetworkEvent fireEffect(BackfireEntity entity, Element element, float amount) {
        return fireEffect(entity.getWorld(), entity.getPos(), entity.minRadius, entity.maxRadius, element, amount, ShapeContext.of(entity));
    }


    public static TheWorkNetworkEvent fireEffect(World world, Vec3d position, float minRadius, float maxRadius, Element element, float amount, ShapeContext raycastIgnore) {
        var list = BACKFIRE_REGISTRY.get(element);

        int highestAffordable = -1;
        for (int i = 0; i < list.size(); i++) {
            var entry = list.get(i);

            if (entry.cost <= amount)
                highestAffordable = i;
            else
                break;
        }

        //If nothing can be afforded, consume everything.
        if (highestAffordable == -1)
            return TheWorkNetworkEvents.NONE;

        var random = world.getRandom().nextInt(highestAffordable + 1);
        var entry = list.get(random);

        if (world.getRandom().nextFloat() > entry.chance)
            return TheWorkNetworkEvents.NONE;

        var direction = Vec3d.fromPolar(MathHelper.lerp(1 - MathHelper.sqrt(world.random.nextFloat()), 5, -90f), world.random.nextFloat() * 360);
        var destination = position.add(direction.multiply(maxRadius));
        var rayOrigin = position.add(direction.multiply(minRadius));

        var raycast = new RaycastContext(rayOrigin, destination, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, raycastIgnore);
        var hitResult = world.raycast(raycast);

        if(hitResult.isInsideBlock())
            return TheWorkNetworkEvents.NONE;

        if (hitResult.getType() == HitResult.Type.MISS )
            hitResult = hitResult.withBlockPos(BlockPos.ofFloored(destination)).withSide(Direction.getFacing(-direction.x, -direction.y, -direction.z));

        return entry.fireEffect(world, hitResult, rayOrigin);
    }

    public static abstract class ElementalBackfireEffect {

        /**
         * How much elemental energy needs to be spent to cause this effect to happen.
         */
        public float cost;

        /**
         * Fractional chance of this effect not being skipped when chosen.
         */
        public float chance = 1;

        public ElementalBackfireEffect withCost(float cost) {
            this.cost = cost;
            return this;
        }

        public ElementalBackfireEffect withChance(float chance) {
            this.chance = chance;
            return this;
        }

        public abstract TheWorkNetworkEvent fireEffect(World world, BlockHitResult pos, Vec3d origin);
    }

    public static abstract class ElementalBackfireEvent extends BlockPosEvent {

        public float cost;
        public Element element;

        public ElementalBackfireEvent(Identifier id) {
            super(id);
        }

        public ElementalBackfireEvent(float cost, Element element, BlockPos pos, Identifier id) {
            super(pos, id);

            this.cost = cost;
            this.element = element;
        }

        @Override
        public void writePacket(PacketByteBuf buf) {
            super.writePacket(buf);

            buf.writeIdentifier(element.id);
        }

        @Override
        public void readPacket(PacketByteBuf buf) {
            super.readPacket(buf);

            this.element = Elements.getElement(buf.readIdentifier());
        }
    }
}
