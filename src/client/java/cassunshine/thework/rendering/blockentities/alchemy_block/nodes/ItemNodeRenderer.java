package cassunshine.thework.rendering.blockentities.alchemy_block.nodes;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.alchemy_circle.nodes.AlchemyNode;
import cassunshine.thework.blockentities.alchemy_circle.nodes.ItemNodeType;
import cassunshine.thework.blocks.TheWorkBlocks;
import cassunshine.thework.rendering.util.RenderingUtilities;
import com.mojang.authlib.exceptions.MinecraftClientException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class ItemNodeRenderer extends SpriteNodeRenderer {
    public static final Identifier SPRITE = new Identifier(TheWorkMod.ModID, "textures/item/item.png");

    @Override
    protected Identifier getSprite() {
        return SPRITE;
    }

    @Override
    public void render(AlchemyNode node) {
        super.render(node);

        if (node.item.isEmpty())
            return;

        World world = node.ring.blockEntity.getWorld();
        BlockPos pos = new BlockPos((int) node.position.x, (int) node.position.y + 4, (int) node.position.z);

        var blockLight = world.getLightLevel(LightType.BLOCK, pos);
        var skyLight = world.getLightLevel(LightType.SKY, pos);

        int light = LightmapTextureManager.pack(blockLight, skyLight);

        if (node.item.getItem() instanceof BlockItem bi && bi.getBlock() == TheWorkBlocks.ALCHEMY_JAR_BLOCK) {
            RenderingUtilities.translateMatrix(0, -1 / 32.0f, 0);
            RenderingUtilities.renderBlock(bi.getBlock().getDefaultState(), light, OverlayTexture.DEFAULT_UV);
        } else {
            float random = (node.position.hashCode() / 100.0f) % 1.0f;
            float time = MinecraftClient.getInstance().world.getTime() + MinecraftClient.getInstance().getTickDelta();

            RenderingUtilities.translateMatrix(0.5f, ((MathHelper.sin(random + (time / 10.0f)) + 1.0f) / 2.0f) * 0.2f, 0.5f);
            RenderingUtilities.rotateMatrix(0, (time / 80.0f) * MathHelper.TAU, 0);
            RenderingUtilities.renderItem(node.item, world, light, OverlayTexture.DEFAULT_UV);
        }
    }
}
