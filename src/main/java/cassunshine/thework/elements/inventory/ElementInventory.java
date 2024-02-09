package cassunshine.thework.elements.inventory;

import cassunshine.thework.elements.Element;
import cassunshine.thework.elements.Elements;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ElementInventory {
    public final Object2FloatMap<Element> amounts = new Object2FloatOpenHashMap<>();

    public float capacity = Float.POSITIVE_INFINITY;

    public ElementInventory() {

    }

    public ElementInventory(float capacity) {
        this.capacity = capacity;
    }

    public void transferAll(ElementInventory target, float amount) {
        for (Element element : amounts.keySet())
            transfer(target, element, amount);
    }

    public void transfer(ElementInventory target, Element element, float amount) {
        float has = amounts.getFloat(element);
        has -= target.add(element, amount);

        if (has <= 0)
            amounts.removeFloat(element);
        else
            amounts.put(element, has);
    }

    public float add(Element element, float amount) {
        float has = amounts.getFloat(element);
        float newAmount = Math.min(has + amount, capacity);
        float added = newAmount - has;

        amounts.put(element, newAmount);

        return added;
    }

    public boolean take(Element element, float amount) {
        float has = amounts.getFloat(element);

        if (has < amount)
            return false;

        float newAmount = has - amount;
        if (newAmount == 0)
            amounts.removeFloat(element);
        else
            amounts.put(element, newAmount);

        return true;
    }

    public boolean has(Element element, float amount) {
        return amounts.getFloat(element) >= amount;
    }


    public NbtCompound writeNbt(NbtCompound compound) {

        for (Object2FloatMap.Entry<Element> entry : amounts.object2FloatEntrySet())
            compound.putFloat(entry.getKey().id.toString(), entry.getFloatValue());

        return compound;
    }

    public void readNbt(NbtCompound compound) {
        for (String key : compound.getKeys()) {
            Element element = Elements.getElement(new Identifier(key));

            if (element == Elements.NONE) continue;

            amounts.put(element, compound.getFloat(key));
        }
    }

    public void flush() {
        amounts.clear();
    }

    public boolean canFit(Element element, float amount) {
        return (amounts.containsKey(element) ? amounts.getFloat(element) + amount : amount) < capacity;
    }
}
