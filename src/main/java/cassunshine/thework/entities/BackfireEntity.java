package cassunshine.thework.entities;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.backfire.BackfireEffects;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import cassunshine.thework.network.events.bookevents.DiscoverMechanicEvent;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Manages the backfire caused by alchemical reactions.
 */
public class BackfireEntity extends Entity {

    public Element element;
    public float amount;

    public float minRadius = 0;
    public float maxRadius = 30;

    public int cooldown;

    public BackfireEntity(EntityType<?> type, World world) {
        super(TheWorkEntities.BACKFIRE_ENTITY_TYPE, world);

        noClip = true;
        calculateDimensions();

        //cooldown = 40;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void tick() {
        super.tick();

        if (amount == 0)
            discard();

        if (getWorld().isClient)
            return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        for (int i = 0; i < 5 && amount > 0; i++) {
            var event = BackfireEffects.fireEffect(this, element, amount);

            if (event instanceof BackfireEffects.ElementalBackfireEvent elementalEvent)
                amount -= elementalEvent.cost;

            TheWorkNetworkEvents.sendEvent(getBlockPos(), getWorld(), event);
        }

        TheWorkNetworkEvents.sendBookLearnEvent(getBlockPos(), getWorld(), new DiscoverMechanicEvent(new Identifier(TheWorkMod.ModID, "backfire")));
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        element = Elements.getElement(new Identifier(nbt.getString("element")));
        amount = nbt.getFloat("amount");
        cooldown = nbt.getInt("cooldown");
        minRadius = nbt.getFloat("min_radius");
        maxRadius = nbt.getFloat("max_radius");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("element", element.id.toString());
        nbt.putFloat("amount", amount);
        nbt.putInt("cooldown", cooldown);
        nbt.putFloat("min_radius", minRadius);
        nbt.putFloat("max_radius", maxRadius);
    }
}
