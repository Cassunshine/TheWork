package cassunshine.thework.data.recipes;

import cassunshine.thework.alchemy.chemistry.ChemistryWorkType;
import cassunshine.thework.alchemy.elements.ElementPacket;

import java.util.Arrays;

public class ReactionRecipe {

    /**
     * If the temperature is above this, the reaction won't occur.
     */
    public float maxTemperature = Float.POSITIVE_INFINITY;
    /**
     * If the temperature is below this, the reaction won't occur.
     */
    public float minTemperature = Float.NEGATIVE_INFINITY;

    /**
     * The required type of work (if any) for the reaction to take place
     */
    public ChemistryWorkType requiredWork = ChemistryWorkType.NONE;


    /**
     * The inputs for the reaction.
     */
    public ElementPacket[] inputs;

    /**
     * The outputs of the reaction.
     */
    public ElementPacket[] outputs;

    /**
     * The heat change this reaction will induce in its container.
     */
    public float heatOutput = 0;

    /**
     * The order of the reaction.
     * <p>
     * Order 2 - Only prime elements
     * Order 1 - Prime and Compound elements
     * Order 0 - Exclusively Compound elements
     */
    public int order = 0;


    @Override
    public String toString() {
        return "ReactionRecipe{" +
                "maxTemperature=" + maxTemperature +
                ", minTemperature=" + minTemperature +
                ", requiredWork=" + requiredWork +
                ", inputs=" + Arrays.toString(inputs) +
                ", outputs=" + Arrays.toString(outputs) +
                ", heatOutput=" + heatOutput +
                ", order=" + order +
                '}';
    }
}
