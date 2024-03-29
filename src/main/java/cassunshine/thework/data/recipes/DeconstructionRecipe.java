package cassunshine.thework.data.recipes;

import cassunshine.thework.alchemy.elements.ElementPacket;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public record DeconstructionRecipe(Identifier id, int time, ElementPacket[] output) {

    @Override
    public String toString() {
        return "DeconstructionRecipe{" +
                "id=" + id +
                ", time=" + time +
                ", output=" + Arrays.toString(output) +
                '}';
    }
}
