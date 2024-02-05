package cassunshine.thework.blockentities.alchemy_circle;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.blockentities.TheWorkBlockEntities;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeType;
import cassunshine.thework.blockentities.alchemy_circle.nodes.NodeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class AlchemyCircleBlockEntity extends BlockEntity {

    public final ArrayList<MainCircle> mainCircles = new ArrayList<>();

    public AlchemyCircleBlockEntity(BlockPos pos, BlockState state) {
        super(TheWorkBlockEntities.ALCHEMY_CIRCLE_TYPE, pos, state);
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        {
            NbtCompound circles = new NbtCompound();
            nbt.put("circles", circles);

            circles.putInt("count", mainCircles.size());

            for (int i = 0; i < mainCircles.size(); i++) {
                MainCircle circle = mainCircles.get(i);
                NbtCompound circleCompound = new NbtCompound();
                circle.writeNbt(circleCompound);
                circles.put(Integer.toString(i), circleCompound);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        {
            mainCircles.clear();

            NbtCompound circles = nbt.getCompound("circles");
            int count = circles.getInt("count");

            for (int i = 0; i < count; i++) {
                MainCircle circle = new MainCircle();
                NbtCompound circleCompound = circles.getCompound(Integer.toString(i));

                circle.readNbt(circleCompound);

                mainCircles.add(circle);
            }
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public Vec3d getCenter() {
        return new Vec3d(getPos().getX() + 0.5f, getPos().getY() - 0.5f, getPos().getZ() + 0.5f);
    }

    public void addCircle(float radius) {

        for (int i = 0; i < mainCircles.size(); i++) {
            MainCircle circle = mainCircles.get(i);

            var dist = Math.abs(circle.radius - radius);

            if (dist < 1) {
                mainCircles.remove(i);

                if (mainCircles.isEmpty()) {
                    markRemoved();
                    world.setBlockState(getPos(), Blocks.AIR.getDefaultState());
                }

                return;
            }

        }

        MainCircle circle = new MainCircle();
        circle.setRadius(radius);

        mainCircles.add(circle);
        mainCircles.sort(Comparator.comparingDouble(a -> a.radius));

        markDirty();
    }


    public boolean onChalkUsed(ItemUsageContext context) {
        var center = getCenter().withAxis(Direction.Axis.Y, 0);
        var hitPos = context.getHitPos().withAxis(Direction.Axis.Y, 0);

        var delta = hitPos.subtract(center).normalize();
        var dist = (float) center.distanceTo(hitPos);

        float angle = (float) Math.atan2(-delta.z, delta.x);

        //Check if we clicked on one of the main lines.
        for (int i = 0; i < mainCircles.size(); i++) {
            AlchemyCircleBlockEntity.MainCircle circle = mainCircles.get(i);

            if (circle.onChalkUsed(context, dist, angle))
                return true;
        }
        return false;
    }

    public class MainCircle {
        public float radius;
        public float circumference;

        public NodeType[] nodes;

        protected void writeNbt(NbtCompound nbt) {
            nbt.putFloat("radius", radius);

            {
                NbtCompound nodesNbt = new NbtCompound();
                nbt.put("nodes", nodesNbt);

                for (int i = 0; i < nodes.length; i++) {
                    NodeType node = nodes[i];
                    nodesNbt.putString(Integer.toString(i), node.id.toString());
                }
            }
        }

        public void readNbt(NbtCompound nbt) {
            setRadius(nbt.getFloat("radius"));

            {
                NbtCompound nodesNbt = nbt.getCompound("nodes");
                for (int i = 0; i < nodes.length; i++) {
                    String val = nodesNbt.getString(Integer.toString(i));
                    nodes[i] = NodeTypes.getType(new Identifier(val));
                }
            }
        }

        public void setRadius(float radius) {
            this.radius = radius;
            this.circumference = 2 * MathHelper.PI * radius;

            if (radius < 1)
                nodes = new NodeType[0];
            else {
                var nodeCount = MathHelper.ceil(radius / 2) * 4;
                nodes = new NodeType[nodeCount];
                Arrays.fill(nodes, NodeTypes.NONE);
            }
        }

        public boolean onChalkUsed(ItemUsageContext context, float distFromCenter, float angleFromCenter) {
            var distToCircle = Math.abs(distFromCenter - radius);

            //If the location clicked is >0.5 blocks away, we definitely didn't click this circle.
            if (distToCircle > 0.5f)
                return false;

            float anglePercent = (angleFromCenter) / MathHelper.TAU;
            anglePercent += 0.25f;
            if (anglePercent < 0)
                anglePercent += 1.0f;
            int nearestIndex = (Math.round(anglePercent * nodes.length)) % nodes.length;
            float nearestPercent = nearestIndex / (float) nodes.length;

            float distToPercent = Math.min(
                    Math.abs(anglePercent - nearestPercent),
                    Math.abs((1 - anglePercent) - nearestPercent)
            );

            float distanceReal = distToPercent * circumference;

            if (distanceReal < 0.6f) {
                var newType = getTargetNodeType(context);

                if (nodes[nearestIndex] != newType)
                    nodes[nearestIndex] = newType;
                else
                    nodes[nearestIndex] = NodeTypes.NONE;

                return true;
            }


            return true;
        }

        private NodeType getTargetNodeType(ItemUsageContext context) {
            Hand oppositeHand = context.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            ItemStack oppositeHandStack = context.getPlayer().getStackInHand(oppositeHand);

            if (oppositeHandStack.getItem() != Items.WRITABLE_BOOK || !oppositeHandStack.hasNbt())
                return NodeTypes.NONE;
            var list = oppositeHandStack.getNbt().getList("pages", NbtElement.STRING_TYPE);
            if (list.isEmpty())
                return NodeTypes.NONE;

            var firstPage = list.getString(0).trim();

            try {
                var id = new Identifier(TheWorkMod.ModID, firstPage);
                return NodeTypes.getType(id);
            } catch (Exception e) {
                //Ignored.
            }

            return NodeTypes.NONE;
        }
    }
}
