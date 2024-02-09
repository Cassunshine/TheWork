package cassunshine.thework.alchemy.circle;

import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.blockentities.alchemycircle.AlchemyCircleBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.Comparator;

public class AlchemyCircle implements AlchemyCircleComponent {

    public final AlchemyCircleBlockEntity blockEntity;

    public final ArrayList<AlchemyRing> rings = new ArrayList<>();

    public AlchemyCircle(AlchemyCircleBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    /**
     * Adds a new ring at the specified radius to the circle.
     */
    public void addRing(float radius) {
        var ring = new AlchemyRing(this);
        ring.setRadius(radius);

        rings.add(ring);
        sortRings();
    }


    private void sortRings() {
        rings.sort(Comparator.comparingDouble(a -> a.radius));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList ringsList = new NbtList();

        //Write lists to NBT
        for (int i = 0; i < rings.size(); i++) ringsList.add(rings.get(i).writeNbt(new NbtCompound()));

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
        }
    }
}
