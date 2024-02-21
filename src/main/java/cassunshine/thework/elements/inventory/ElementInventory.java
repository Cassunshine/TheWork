package cassunshine.thework.elements.inventory;

import cassunshine.thework.elements.Element;
import cassunshine.thework.elements.Elements;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.function.Predicate;

public class ElementInventory {
    private final float[] amounts = new float[Elements.getElementCount()];

    public float capacity = Float.POSITIVE_INFINITY;

    public ElementInventory() {

    }

    public ElementInventory withCapacity(float capacity) {
        this.capacity = capacity;
        return this;
    }

    protected void setAmount(Element element, float amount) {
        amounts[element.number] = amount;
    }

    /**
     * Gets how much of an element the inventory has.
     */
    public float get(Element element) {
        return amounts[element.number];
    }

    /**
     * Returns true if inventory has at least as much as the specified element.
     */
    public boolean has(Element element, float amount) {
        return get(element) >= amount;
    }

    public boolean empty(){
        for (int i = 0; i < Elements.getElementCount(); i++)
            if (get(Elements.getElement(i)) > 0)
                return false;

        return true;
    }

    /**
     * Puts some amount of element in the inventory, returning the leftover that couldn't fit.
     */
    public float put(Element element, float amount) {
        //Get current amount, for later.
        float prevAmount = get(element);

        //Put new amount into dictionary
        float newAmount = Math.min(prevAmount + amount, capacity);
        setAmount(element, newAmount);

        //Calculate how much we added to this inventory.
        float added = newAmount - prevAmount;

        //Return anything we didn't add.
        return amount - added;
    }

    /**
     * Transfers up to the specified amount from this inventory into the target.
     */
    public void transfer(ElementInventory target, float amount) {

        for (int i = 0; i < Elements.getElementCount(); i++) {
            float mine = get(Elements.getElement(i));

            if(mine == 0)
                continue;

            var element = Elements.getElement(i);

            //'remove' the amount we gave.
            float giveValue = Math.min(amount, mine);
            mine -= giveValue;

            //Add to target inventory, recording how much it gave back.
            float leftover = target.put(element, giveValue);
            mine += leftover;

            //Set to how much we have now.
            setAmount(element, mine);
        }
    }

    public void transferSingle(ElementInventory target, float amount) {

        for (int i = 0; i < Elements.getElementCount(); i++) {
            var element = Elements.getElement(i);
            float mine = get(element);

            if (mine == 0)
                continue;

            //'remove' the amount we gave.
            float giveValue = Math.min(amount, mine);
            mine -= giveValue;

            //Add to target inventory, recording how much it gave back.
            float leftover = target.put(element, giveValue);
            mine += leftover;

            //Set to how much we have now.
            setAmount(element, mine);
            return;
        }
    }


    /**
     * Transfers up to the specified amount from this inventory into the target.
     */
    public void transfer(ElementInventory target, float amount, Predicate<Element> allowedElements) {

        for (int i = 0; i < Elements.getElementCount(); i++) {
            float mine = get(Elements.getElement(i));
            var element = Elements.getElement(i);

            if (!allowedElements.test(element) || mine == 0)
                continue;

            //'remove' the amount we gave.
            float giveValue = Math.min(amount, mine);
            mine -= giveValue;

            //Add to target inventory, recording how much it gave back.
            float leftover = target.put(element, giveValue);
            mine += leftover;

            //Set to how much we have now.
            setAmount(element, mine);
        }
    }

    public boolean give(Element element, float amount) {
        float mine = get(element);

        if (mine < amount)
            return false;

        setAmount(element, mine - amount);
        return true;
    }

    //Takes up to the specified amount.
    //Returns how much it ACTUALLY took.
    public float take(Element element, float amount) {
        float mine = get(element);
        float taken = Math.min(amount, mine);

        setAmount(element, mine - taken);

        return taken;
    }

    public boolean canFit(Element element, float amount) {
        return get(element) + amount < capacity;
    }

    public void clear() {
        Arrays.fill(amounts, 0);
    }


    public NbtCompound writeNbt(NbtCompound compound) {
        for (int i = 0; i < amounts.length; i++) {
            var amount = amounts[i];
            var element = Elements.getElement(i);

            compound.putFloat(element.id.toString(), amount);
        }

        return compound;
    }

    public void readNbt(NbtCompound compound) {
        for (String key : compound.getKeys()) {
            var element = Elements.getElement(new Identifier(key));
            put(element, compound.getFloat(key));
        }
    }
}
