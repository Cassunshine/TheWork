package cassunshine.thework.alchemy.circle.node;

import cassunshine.thework.alchemy.circle.AlchemyCircleComponent;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeType;
import cassunshine.thework.alchemy.circle.node.type.AlchemyNodeTypes;
import cassunshine.thework.alchemy.circle.ring.AlchemyRing;
import cassunshine.thework.elements.inventory.ElementInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class AlchemyNode implements AlchemyCircleComponent {

    /**
     * The ring this node is attached to.
     */
    public final AlchemyRing ring;

    public final int index;

    /**
     * Inventory holding all the elements the node currently has.
     */
    public final ElementInventory inventory = new ElementInventory();


    /**
     * The type of this node.
     */
    public AlchemyNodeType nodeType = AlchemyNodeTypes.NONE;

    /**
     * Custom data for this node, stored by the type.
     */
    public AlchemyNodeType.Data typeData = AlchemyNodeType.Data.NONE;


    /**
     * Determines if the node should output to the next path in the sequence.
     */
    public boolean outputMain;
    /**
     * Determines if the node should output to the secondary path.
     */
    public boolean outputSecondary;


    /**
     * The ItemStack this node is holding, if any.
     */
    public ItemStack heldStack = ItemStack.EMPTY;

    public AlchemyNode(AlchemyRing ring, int index) {
        this.ring = ring;
        this.index = index;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("type", nodeType.id.toString());
        nbt.put("typeData", typeData.writeNbt(new NbtCompound()));

        nbt.putBoolean("outputMain", outputMain);
        nbt.putBoolean("outputSecondary", outputSecondary);

        nbt.put("item", heldStack.writeNbt(new NbtCompound()));
        nbt.put("inventory", inventory.writeNbt(new NbtCompound()));

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        nodeType = AlchemyNodeTypes.get(new Identifier(nbt.getString("type")));
        typeData = nodeType.getData();
        typeData.readNbt(nbt.getCompound("typeData"));

        outputMain = nbt.getBoolean("outputMain");
        outputSecondary = nbt.getBoolean("outputSecondary");

        heldStack = ItemStack.fromNbt(nbt.getCompound("item"));
        inventory.readNbt(nbt.getCompound("inventory"));
    }
}

