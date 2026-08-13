package com.baisylia.modestmagic.block.custom;

import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;
import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import com.baisylia.modestmagic.client.ModSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AltarBlock extends PedestalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), PedestalBlockEntity::tick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new AltarBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof AltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (state.getValue(POWERED)) {
            return pedestalUse(level, pos, player, hand, state, ModSounds.ADD_ITEM_ALTAR.get());
        }

        if (altar.tryCraft()) {
            return InteractionResult.SUCCESS_SERVER;
        }

        return pedestalUse(level, pos, player, hand, state, ModSounds.ADD_ITEM_ALTAR.get());
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof AltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (state.getValue(POWERED)) {
            return pedestalUse(level, pos, player, InteractionHand.MAIN_HAND, state, ModSounds.ADD_ITEM_ALTAR.get());
        }

        if (altar.tryCraft()) {
            return InteractionResult.SUCCESS_SERVER;
        }

        return pedestalUse(level, pos, player, InteractionHand.MAIN_HAND, state, ModSounds.ADD_ITEM_ALTAR.get());
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @org.jspecify.annotations.Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) return;

        boolean isPowered = level.hasNeighborSignal(pos);
        boolean wasPowered = state.getValue(POWERED);

        if (isPowered != wasPowered) {
            level.setBlock(pos, state.setValue(POWERED, isPowered), 3);
        }

        if (isPowered && !wasPowered) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AltarBlockEntity altar) {
                altar.tryCraft();
            }
        }
    }
}