package com.baisylia.modestmagic.block.custom;

import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;
import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import com.baisylia.modestmagic.client.ModSounds;
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
        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof AltarBlockEntity table))
            return InteractionResult.PASS;

        // Craft
        if(player.isShiftKeyDown()) {
            boolean crafted = table.tryCraft();
            if(crafted) return InteractionResult.CONSUME;
        }

        // Pedestal
        return pedestalUse(level, pos, player, hand, state, ModSounds.ADD_ITEM_ALTAR.get());
    }
}