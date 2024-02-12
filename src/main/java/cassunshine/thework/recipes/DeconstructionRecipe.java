package cassunshine.thework.recipes;

import cassunshine.thework.elements.ElementPacket;
import net.minecraft.util.Identifier;

public record DeconstructionRecipe(Identifier id, int time, ElementPacket[] output) {
}
