package cassunshine.thework.alchemy.backfire;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.alchemy.elements.Element;
import cassunshine.thework.network.events.BlockPosEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvent;
import cassunshine.thework.network.events.TheWorkNetworkEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;

public class PlaceBlockBackfireEffect extends BackfireEffects.ElementalBackfireEffect {

    private final Random selectorRandom = new Random();

    private final BlockState[] possibleStates;
    private final Element element;

    private BlockState next;

    public PlaceBlockBackfireEffect(Element element, BlockState... states) {
        this.element = element;

        possibleStates = states;
        selectNext();
    }

    private void selectNext() {
        next = possibleStates[selectorRandom.nextInt(possibleStates.length)];
    }

    @Override
    public TheWorkNetworkEvent fireEffect(World world, BlockHitResult hit, Vec3d origin) {
        var realPos = hit.getBlockPos().add(hit.getSide().getVector());

        if (!next.canPlaceAt(world, realPos) || !world.getBlockState(realPos).isReplaceable())
            return TheWorkNetworkEvents.NONE;

        var event = new Event(next, hit.getPos(), origin, cost, element, realPos);

        next = possibleStates[selectorRandom.nextInt(possibleStates.length)];
        selectNext();

        return event;
    }


    public static class Event extends BackfireEffects.ElementalBackfireEvent {
        public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "backfire_place_block");

        public BlockState state;
        public Vec3d hitPos;
        public Vec3d originPos;

        public Event() {
            super(IDENTIFIER);
        }

        public Event(BlockState state, Vec3d hitPos, Vec3d originPos, float cost, Element element, BlockPos position) {
            super(cost, element, position, IDENTIFIER);

            this.state = state;
            this.hitPos = hitPos;
            this.originPos = originPos;
        }

        @Override
        public void writePacket(PacketByteBuf buf) {
            super.writePacket(buf);

            buf.writeRegistryValue(Block.STATE_IDS, state);
            buf.writeVec3d(hitPos);
            buf.writeVec3d(originPos);
        }

        @Override
        public void readPacket(PacketByteBuf buf) {
            super.readPacket(buf);

            state = buf.readRegistryValue(Block.STATE_IDS);
            hitPos = buf.readVec3d();
            originPos = buf.readVec3d();
        }

        @Override
        public void applyToWorld(World world) {
            super.applyToWorld(world);

            world.setBlockState(position, state);
            world.playSoundAtBlockCenter(position, state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, state.getSoundGroup().volume, state.getSoundGroup().pitch, true);
            //world.addBlockBreakParticles(position, state);
        }
    }
}
