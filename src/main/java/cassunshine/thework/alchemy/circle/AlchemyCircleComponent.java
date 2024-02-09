package cassunshine.thework.alchemy.circle;

import net.minecraft.nbt.NbtCompound;

public interface AlchemyCircleComponent {

    NbtCompound writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);

}
