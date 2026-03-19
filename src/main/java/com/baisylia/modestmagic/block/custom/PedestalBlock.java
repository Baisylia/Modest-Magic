package com.baisylia.modestmagic.block.custom;

import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import com.baisylia.modestmagic.client.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HAS_ITEM = BooleanProperty.create("has_item");
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public PedestalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HAS_ITEM, false)
                .setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_ITEM, TOP, BOTTOM, AXIS, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis axis = context.getClickedFace().getAxis();

        boolean water = context.getLevel()
                .getFluidState(context.getClickedPos())
                .getType() == Fluids.WATER;

        return defaultBlockState()
                .setValue(AXIS, axis)
                .setValue(WATERLOGGED, water);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        Direction.Axis axis = state.getValue(AXIS);

        Direction positive = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        Direction negative = Direction.get(Direction.AxisDirection.NEGATIVE, axis);

        BlockState front = level.getBlockState(pos.relative(positive));
        BlockState back = level.getBlockState(pos.relative(negative));

        boolean connectTop =
                front.getBlock() instanceof PedestalBlock &&
                        front.getValue(AXIS) == axis;

        boolean connectBottom =
                back.getBlock() instanceof PedestalBlock &&
                        back.getValue(AXIS) == axis;

        boolean newTop = !connectTop;
        boolean newBottom = !connectBottom;

        BlockState newState = state
                .setValue(TOP, newTop)
                .setValue(BOTTOM, newBottom);

        if (level instanceof Level realLevel) {
            validateItem(realLevel, pos, newState);
        }

        return newState;
    }

    private void validateItem(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PedestalBlockEntity pedestal)) return;

        boolean invalid = state.getValue(AXIS) != Direction.Axis.Y || !state.getValue(TOP);

        if (invalid && !pedestal.getItem().isEmpty()) {
            ItemStack stack = pedestal.getItem();
            Containers.dropItemStack(level, pos.getX(), pos.getY() + 1, pos.getZ(), stack);

            BlockState newState = state.setValue(HAS_ITEM, false);
            pedestal.clear();
            pedestal.setChanged();
            level.sendBlockUpdated(pos, state, newState, 3);
            level.setBlock(pos, newState, 3);
            if (level instanceof ServerLevel server) {
                server.getChunkSource().blockChanged(pos);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);

        if (oldState.getBlock() == state.getBlock()) {
            validateItem(level, pos, state);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PedestalBlockEntity(pPos, pState);
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction.Axis axis) {
        if (axis == Direction.Axis.Y) return shape;

        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            if (axis == Direction.Axis.X) {
                buffer[1] = Shapes.or(buffer[1], Block.box(
                        minY * 16, minX * 16, minZ * 16,
                        maxY * 16, maxX * 16, maxZ * 16
                ));
            }
            if (axis == Direction.Axis.Z) {
                buffer[1] = Shapes.or(buffer[1], Block.box(
                        minX * 16, minZ * 16, minY * 16,
                        maxX * 16, maxZ * 16, maxY * 16
                ));
            }
        });
        return buffer[1];
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        boolean top = state.getValue(TOP);
        boolean bottom = state.getValue(BOTTOM);
        Direction.Axis axis = state.getValue(AXIS);

        VoxelShape column = Block.box(3,0,3,13,16,13);

        VoxelShape topShape = Block.box(0, 13, 0, 16, 16, 16);
        VoxelShape bottomShape = Block.box(0, 0, 0, 16, 3, 16);

        VoxelShape shape = column;

        if (top) shape = Shapes.or(shape, topShape);
        if (bottom) shape = Shapes.or(shape, bottomShape);

        return rotateShape(shape, axis);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return pedestalUse(level, pos, player, hand, state, ModSounds.ADD_ITEM_PEDESTAL.get());
    }

    protected InteractionResult pedestalUse(Level level, BlockPos pos, Player player, InteractionHand hand, BlockState state, @NotNull SoundEvent soundEvent) {
        if(state.getValue(AXIS) != Direction.Axis.Y) return InteractionResult.PASS;
        if(!state.getValue(TOP)) return InteractionResult.PASS;

        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof PedestalBlockEntity pedestal))
            return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);

        if(pedestal.getItem().isEmpty()) {
            if(held.isEmpty()) return InteractionResult.PASS;

            if(!level.isClientSide) {
                pedestal.setItem(held.split(1));
            }

            level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        else {
            if(!level.isClientSide) {
                ItemStack stack = pedestal.getItem();
                pedestal.clear();
                level.playSound(null, pos, ModSounds.REMOVE_ITEM_PEDESTAL.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

                if(!player.addItem(stack)) {
                    player.drop(stack, false);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);

            if(be instanceof PedestalBlockEntity pedestal) {
                ItemStack stack = pedestal.getItem();

                if(!stack.isEmpty()) {
                    Containers.dropItemStack(level,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            stack);
                }
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }
}
