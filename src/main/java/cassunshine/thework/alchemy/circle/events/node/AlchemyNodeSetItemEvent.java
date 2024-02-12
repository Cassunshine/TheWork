package cassunshine.thework.alchemy.circle.events.node;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AlchemyNodeSetItemEvent extends AlchemyNodeEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "node_set_item");

    public ItemStack stack;

    public AlchemyNodeSetItemEvent() {
        super(IDENTIFIER);
    }

    public AlchemyNodeSetItemEvent(ItemStack stack, AlchemyNode node) {
        super(node, IDENTIFIER);

        this.stack = stack.copy();
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeNbt(stack.writeNbt(new NbtCompound()));
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        stack = ItemStack.fromNbt(buf.readNbt());
    }

    @Override
    public void applyToNode(AlchemyNode node) {
        super.applyToNode(node);

        node.heldStack = stack;
    }
}
