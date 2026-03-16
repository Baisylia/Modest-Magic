package com.baisylia.modestmagic.block.custom;

import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;
import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class AltarBlock extends PedestalBlock {

    public AltarBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof AltarBlockEntity table))
            return InteractionResult.PASS;

        if(state.getValue(AXIS) != Direction.Axis.Y)
            return InteractionResult.PASS;
        if(state.getValue(TOP) == false)
            return InteractionResult.PASS;

        // Craft
        if(player.isShiftKeyDown()) {
            boolean crafted = table.tryCraft();
            return crafted ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        // Pedestal
        PedestalBlockEntity pedestal = (PedestalBlockEntity) level.getBlockEntity(pos);
        ItemStack held = player.getItemInHand(hand);

        if(pedestal.getItem().isEmpty()) {
            if(!held.isEmpty()) {
                pedestal.setItem(held.split(1));
                return InteractionResult.CONSUME;
            }
        } else {
            ItemStack stack = pedestal.getItem();
            pedestal.clear();

            if(!player.addItem(stack)) {
                player.drop(stack, false);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}