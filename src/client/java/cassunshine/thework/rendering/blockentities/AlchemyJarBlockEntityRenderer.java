package cassunshine.thework.rendering.blockentities;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.blockentities.jar.AlchemyJarBlockEntity;
import cassunshine.thework.rendering.util.RenderingUtilities;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AlchemyJarBlockEntityRenderer implements BlockEntityRenderer<AlchemyJarBlockEntity> {
    private static final ModelIdentifier ALCHEMY_JAR_MODEL_ID = new ModelIdentifier(TheWorkMod.ModID, "block/alchemy_jar", "");

    public AlchemyJarBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(AlchemyJarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        RenderingUtilities.setupStack(matrices);
        RenderingUtilities.setupConsumers(vertexConsumers);
        RenderingUtilities.setupRenderLayer(RenderLayer.getEntityCutoutNoCull(new Identifier(TheWorkMod.ModID, "textures/block/alchemy_jar.png")));

        RenderingUtilities.setupLightOverlay(light, overlay);
        renderNormalLiquid(entity.createNbt());
    }

    public static void renderNormalLiquid(NbtCompound nbt){
        RenderingUtilities.setupNormal(0, 1, 0);

        addLiquid(nbt, (x, y, z, u, v, color) -> {
            RenderingUtilities.setupColor(color);
            RenderingUtilities.saneVertex(x, y, z, u, v);
        }, ()->{});
    }

    public static void addLiquid(NbtCompound nbt, LiquidRenderer renderer, Runnable perFace) {
        if (nbt == null || !nbt.contains("element"))
            return;

        float amount = nbt.getInt("amount");
        var element = Elements.getElement(new Identifier(nbt.getString("element")));

        if (amount == 0 || element == null || element == Elements.NONE)
            return;

        float minCoord = 4.001f / 16.0f;
        float maxCoord = 11.99f / 16.0f;
        float heightMin = 1.001f / 16.0f;
        float height = (MathHelper.lerp(amount / 2048.0f, 1, 10.99f)) / 16.0f;
        float minUv = 1 - (8 / 32.0f);
        float maxUv = 1;


        //Top
        renderer.generateVertex(minCoord, height, minCoord, minUv, minUv, element.color);
        renderer.generateVertex(maxCoord, height, minCoord, maxUv, minUv, element.color);
        renderer.generateVertex(maxCoord, height, maxCoord, maxUv, maxUv, element.color);
        renderer.generateVertex(minCoord, height, maxCoord, minUv, maxUv, element.color);

        perFace.run();

        renderer.generateVertex(minCoord, heightMin, minCoord, minUv, maxUv, element.color);
        renderer.generateVertex(maxCoord, heightMin, minCoord, maxUv, maxUv, element.color);
        renderer.generateVertex(maxCoord, height, minCoord, maxUv, minUv, element.color);
        renderer.generateVertex(minCoord, height, minCoord, minUv, minUv, element.color);

        perFace.run();

        renderer.generateVertex(minCoord, height, maxCoord, minUv, minUv, element.color);
        renderer.generateVertex(maxCoord, height, maxCoord, maxUv, minUv, element.color);
        renderer.generateVertex(maxCoord, heightMin, maxCoord, maxUv, maxUv, element.color);
        renderer.generateVertex(minCoord, heightMin, maxCoord, minUv, maxUv, element.color);

        perFace.run();

        renderer.generateVertex(minCoord, heightMin, maxCoord, minUv, maxUv, element.color);
        renderer.generateVertex(minCoord, heightMin, minCoord, maxUv, maxUv, element.color);
        renderer.generateVertex(minCoord, height, minCoord, maxUv, minUv, element.color);
        renderer.generateVertex(minCoord, height, maxCoord, minUv, minUv, element.color);

        perFace.run();

        renderer.generateVertex(maxCoord, height, maxCoord, minUv, minUv, element.color);
        renderer.generateVertex(maxCoord, height, minCoord, maxUv, minUv, element.color);
        renderer.generateVertex(maxCoord, heightMin, minCoord, maxUv, maxUv, element.color);
        renderer.generateVertex(maxCoord, heightMin, maxCoord, minUv, maxUv, element.color);

        perFace.run();

        renderer.generateVertex(minCoord, heightMin, maxCoord, minUv, maxUv, element.color);
        renderer.generateVertex(maxCoord, heightMin, maxCoord, maxUv, maxUv, element.color);
        renderer.generateVertex(maxCoord, heightMin, minCoord, maxUv, minUv, element.color);
        renderer.generateVertex(minCoord, heightMin, minCoord, minUv, minUv, element.color);

        perFace.run();
    }


    public interface LiquidRenderer {
        void generateVertex(float x, float y, float z, float u, float v, int color);
    }
}
