package cassunshine.thework.alchemy.chemistry;

import cassunshine.thework.elements.inventory.ElementInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

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

    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit, Vec3d localPos) {
        return ActionResult.PASS;
    }

    public void tick() {


    }

    public VoxelShape getShape() {
        return VoxelShapes.cuboid(0, 0, 0, 0.5f, 1, 0.5f);
    }
}
