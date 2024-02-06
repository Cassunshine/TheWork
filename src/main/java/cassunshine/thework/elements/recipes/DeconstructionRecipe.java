package cassunshine.thework.elements.recipes;

import cassunshine.thework.elements.ElementPacket;
import net.minecraft.util.Identifier;

public record DeconstructionRecipe (Identifier id, ElementPacket[] output) {
}
