package cassunshine.thework.alchemy.chemistry;

import cassunshine.thework.elements.inventory.ElementInventory;

public class ChemistryObject {
    /**
     * The inventory of elements inside of this chemistry object.
     */
    public final ElementInventory inventory = new ElementInventory().withCapacity(128);

    /**
     * The output of the chemistry object, where processed elements (if any) are placed.
     */
    public final ElementInventory output = new ElementInventory().withCapacity(128);

    /**
     * Temperature of this chemistry object.
     * <p>
     * All chemistry objects tend towards 0, aka room temperature.
     * The further from room temperature, the faster they move towards it.
     */
    public float temperature;

    public void tick() {



    }
}
