package cassunshine.thework.client.gui.ingame.notebook.drawables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.Random;

public class RandomItemDisplay extends ItemDisplay {

    private static final Random random = new Random();
    private float changeCooldown = 0f;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        changeCooldown -= delta;

        if (changeCooldown <= 0) {
            var randomItem = Registries.ITEM.get(random.nextInt(Registries.ITEM.size()));
            stack = new ItemStack(randomItem);

            changeCooldown = 10f;
        }

        super.render(context, mouseX, mouseY, delta);
    }
}
