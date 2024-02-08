package cassunshine.thework.blockentities.alchemy_circle.events.node;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


//TODO - Use some other player inventory update method. This feels jank.
public class NodeSwapItemEvent extends AlchemyNodeEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "events/node_swap_item");

    private NbtCompound newNodeStackData;
    private NbtCompound newPlayerStackData;
    private int playerID;

    public NodeSwapItemEvent() {
        super(IDENTIFIER);
    }

    public NodeSwapItemEvent(ItemStack newNodeStack, ItemStack newPlayerStack, PlayerEntity player, AlchemyNode node) {
        super(node, IDENTIFIER);

        newNodeStackData = newNodeStack.writeNbt(new NbtCompound());
        newPlayerStackData = newPlayerStack.writeNbt(new NbtCompound());
        playerID = player.getId();
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);

        buf.writeInt(playerID);
        buf.writeNbt(newNodeStackData);
        buf.writeNbt(newPlayerStackData);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        playerID = buf.readInt();
        newNodeStackData = buf.readNbt();
        newPlayerStackData = buf.readNbt();
    }

    @Override
    public void applyToNode(AlchemyNode node) {
        var world = node.ring.circle.getWorld();

        //Set to node
        {
            node.item = ItemStack.fromNbt(newNodeStackData);
        }

        //Set to player
        if (world.getEntityById(playerID) instanceof PlayerEntity playerEntity) {
            playerEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.fromNbt(newPlayerStackData));
        }
    }
}
