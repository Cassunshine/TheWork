package cassunshine.thework.alchemy.circle.path;

import cassunshine.thework.alchemy.circle.AlchemyCircleComponent;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.elements.Element;
import cassunshine.thework.elements.Elements;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

/**
 * Paths are used in alchemy circles to transport elements from one location to another.
 * <p>
 * They operate in single units of elements, i.e., 1 element unit at a time.
 */
public class AlchemyPath implements AlchemyCircleComponent {
    /**
     * The speed at which elements travel along paths, in blocks per tick.
     */
    public static final float TRAVEL_SPEED = 1 / 20.0f;

    public final AlchemyRing ring;

    public final int index;

    /**
     * List of all elements currently travelling through this path.
     */
    public final ArrayList<ElementInstance> elements = new ArrayList<>();

    /**
     * The physical length of this path.
     */
    public float length = 1;

    public AlchemyPath(AlchemyRing ring, int index, float length) {
        this.ring = ring;
        this.index = index;
        this.length = length;
    }

    /**
     * Moves elements along the path.
     * <p>
     * use removeFinishedElements to remove any elements that reach the end after this.
     */
    public void tick() {
        for (ElementInstance instance : elements)
            instance.progress = MathHelper.clamp(instance.progress + TRAVEL_SPEED, 0, length);
    }

    /**
     * Inserts an element into the path.
     */
    public void addElement(Element element, float progress) {
        elements.add(new ElementInstance(element, progress));
    }

    /**
     * Removes all elements from the path that have reached the end, adding them to a list so we can iterate them later.
     */
    public void removeFinishedElements(ArrayList<Element> results) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            var element = elements.get(i);

            if (element.progress < length)
                continue;

            results.add(element.element);
            elements.remove(i);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtList elementTypes = new NbtList();
        NbtList elementProgresses = new NbtList();

        for (ElementInstance element : elements) {
            elementTypes.add(NbtString.of(element.element.id.toString()));
            elementProgresses.add(NbtFloat.of(element.progress));
        }

        nbt.put("types", elementTypes);
        nbt.put("progresses", elementProgresses);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        elements.clear();

        NbtList elementTypes = nbt.getList("types", NbtElement.STRING_TYPE);
        NbtList elementProgresses = nbt.getList("progresses", NbtElement.FLOAT_TYPE);

        for (int i = 0; i < elementTypes.size(); i++) {
            var type = Elements.getElement(new Identifier(elementTypes.getString(i)));
            var progress = elementProgresses.getFloat(i);

            elements.add(new ElementInstance(type, progress));
        }
    }


    /**
     * java has no value types, makes me vv sad ;-;
     * i want my C# structs back, no boxing for basic values like this please
     */
    public static class ElementInstance {
        public Element element;
        public float progress;

        public ElementInstance(Element element) {
            this(element, 0);
        }

        public ElementInstance(Element element, float progress) {
            this.element = element;
            this.progress = progress;
        }
    }
}
