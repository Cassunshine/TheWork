package cassunshine.thework.alchemy.circle.node.type;

import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class AlchemyNodeType {
    public Identifier id;

    public void activate(AlchemyNode node) {

    }

    public void activeTick(AlchemyNode node) {

    }

    public void deactivate(AlchemyNode node) {

    }

    public AlchemyNodeType.Data getData() {
        return Data.NONE;
    }


    public abstract static class Data {

        public static final AlchemyNodeType.Data NONE = new Data() {
            @Override
            public NbtCompound writeNbt(NbtCompound nbt) {
                return nbt;
            }

            @Override
            public void readNbt(NbtCompound nbt) {

            }
        };

        public abstract NbtCompound writeNbt(NbtCompound nbt);

        public abstract void readNbt(NbtCompound nbt);
    }
}
