package cassunshine.thework.blockentities.jar;

import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blocks.TheWorkBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class AlchemyJarBlockEntity extends BlockEntity {

    public Identifier element = Elements.NONE.id;
    public float amount = 0;

    public AlchemyJarBlockEntity(BlockPos pos, BlockState state) {
        super(TheWorkBlockEntities.ALCHEMY_JAR_TYPE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt == null)
            return;

        super.readNbt(nbt);

        element = new Identifier(nbt.getString("element"));
        amount = nbt.getFloat("amount");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putString("element", element.toString());
        nbt.putFloat("amount", amount);
    }


}
