package cassunshine.thework.client.gui.ingame.notebook.drawables;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;

public class ItemDisplay implements Drawable {
    public int x;
    public int y;

    public ItemStack stack = ItemStack.EMPTY;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        if (stack.isEmpty())
            return;

        var matrices = context.getMatrices();
        var client = MinecraftClient.getInstance();

        BakedModel bakedModel = client.getItemRenderer().getModel(stack, client.world, client.player, 0);
        matrices.push();
        matrices.translate((float) (x), (float) (y), (float) (150));

        try {
            matrices.multiplyPositionMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 0.001f));
            matrices.scale( 48 / (float) client.getWindow().getScaleFactor(), 48 / (float) client.getWindow().getScaleFactor(), 48 / (float) client.getWindow().getScaleFactor());
            boolean bl = !bakedModel.isSideLit();
            if (bl) {
                DiffuseLighting.disableGuiDepthLighting();
            }

            client.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, matrices, context.getVertexConsumers(), 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
            RenderSystem.disableDepthTest();
            context.getVertexConsumers().draw();
            RenderSystem.enableDepthTest();
            if (bl) {
                DiffuseLighting.enableGuiDepthLighting();
            }
        } catch (Throwable var12) {
            //Ignore.
        }

        matrices.pop();
    }
}
