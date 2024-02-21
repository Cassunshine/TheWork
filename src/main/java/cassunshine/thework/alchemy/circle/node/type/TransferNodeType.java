package cassunshine.thework.alchemy.circle.node.type;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.circle.node.AlchemyNode;
import cassunshine.thework.blocks.AlchemyJarBlock;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.elements.Element;
import cassunshine.thework.elements.Elements;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

/**
 * Can pass elements around differently depending on the rune on it.
 */
public class TransferNodeType extends AlchemyNodeType {

    private static Identifier SPLIT = new Identifier(TheWorkMod.ModID, "split");
    private static Identifier ROUND_ROBIN = new Identifier(TheWorkMod.ModID, "round_robin");

    @Override
    public void activeTick(AlchemyNode node) {
        var element = Elements.getElement(node.rune);

        //Transfer nodes with an element turn into a filter node for that element.
        if (node.rune.equals(SPLIT)) {
            if(node.inventory.empty())
                return;

            if (!node.typeData.contains("split_other", NbtElement.BYTE_TYPE))
                node.typeData.putBoolean("split_other", false);
            var val = node.typeData.getBoolean("split_other");
            node.typeData.putBoolean("split_other", !val);

            if (val)
                node.inventory.transferSingle(node.ringOutput, Float.POSITIVE_INFINITY);
            else
                node.inventory.transferSingle(node.linkOutput, Float.POSITIVE_INFINITY);

            return;
        } else if (node.rune.equals(ROUND_ROBIN)) {
            if(node.inventory.empty())
                return;

            if (!node.typeData.contains("rr_index", NbtElement.INT_TYPE))
                node.typeData.putInt("rr_index", 0);

            var val = node.typeData.getInt("rr_index");

            for (int i = 0; i < Elements.getElementCount(); i++) {
                val = (val + 1) % Elements.getElementCount();
                var e = Elements.getElement(val);

                if (!node.inventory.give(e, 1))
                    continue;

                node.linkOutput.put(e, 1);
                break;
            }

            node.typeData.putInt("rr_index", val);
            return;
        } else if(element != Elements.NONE) {
            //Transfer into the jar first, if possible.
             if (node.heldStack.getItem() instanceof BlockItem bi && bi.getBlock() == TheWorkBlocks.ALCHEMY_JAR_BLOCK) {
                var inv = AlchemyJarBlock.getInventoryForStack(node.heldStack);

                node.inventory.transfer(inv, Float.POSITIVE_INFINITY, e -> e == element);
            }

            //Move all elements except the filtered one into the output.
            node.inventory.transfer(node.ringOutput, Float.POSITIVE_INFINITY, e -> e != element);
            //Move everything else into the link output.
            super.activeTick(node);
        }

        super.activeTick(node);
    }
}
