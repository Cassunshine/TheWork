package cassunshine.thework.rendering.model;

import cassunshine.thework.alchemy.elements.Elements;
import cassunshine.thework.rendering.blockentities.AlchemyJarBlockEntityRenderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class AlchemyJarModel implements FabricBakedModel, BakedModel, UnbakedModel {
    public final UnbakedModel vanillaModel;
    public BakedModel vanillaBakedModel;

    public AlchemyJarModel(UnbakedModel vanillaModel) {
        this.vanillaModel = vanillaModel;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        vanillaBakedModel.emitItemQuads(stack, randomSupplier, context);

        if (!stack.hasNbt() || !stack.getOrCreateNbt().contains("element"))
            return;

        var emitter = context.getEmitter();
        var sprite = vanillaBakedModel.getParticleSprite();

        AtomicInteger index = new AtomicInteger(3);

        AlchemyJarBlockEntityRenderer.addLiquid(stack.getNbt(), (x, y, z, u, v, color) -> {
            emitter.pos(index.get(), x, y, z);
            emitter.uv(index.get(), u * 16.0f, v * 16.0f);
            emitter.color(index.get(), color);

            index.getAndDecrement();
        }, ()->{
            emitter.spriteBake(vanillaBakedModel.getParticleSprite(), MutableQuadView.BAKE_LOCK_UV);
            emitter.emit();

            index.set(3);
        });
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        vanillaBakedModel.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return vanillaModel.getModelDependencies();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
        vanillaModel.setParents(modelLoader);
    }

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        vanillaBakedModel = vanillaModel.bake(baker, textureGetter, rotationContainer, modelId);

        return this;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return vanillaBakedModel.getQuads(state, face, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return vanillaBakedModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return vanillaBakedModel.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return vanillaBakedModel.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
}
