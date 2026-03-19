package com.baisylia.modestmagic.block.entity.custom;

import com.baisylia.modestmagic.block.custom.PedestalBlock;
import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class PedestalBlockEntity extends BlockEntity implements WorldlyContainer {

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.PEDESTAL_BLOCK_ENTITY.get(), pos, state);
    }

    protected PedestalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if (level != null && !level.isClientSide) {
                BlockState state = getBlockState();

                level.setBlock(worldPosition, state.setValue(PedestalBlock.HAS_ITEM, !getStackInSlot(0).isEmpty()), 3);

                level.sendBlockUpdated(worldPosition, state, state, 3);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return ItemStack.EMPTY;

            if (!getStackInSlot(slot).isEmpty()) {
                return stack;
            }
            ItemStack single = stack.copy();
            single.setCount(1);

            if (!simulate) {
                setStackInSlot(slot, single);
            }

            ItemStack remainder = stack.copy();
            remainder.shrink(1);
            return remainder;
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public ItemStack getItem() {
        ItemStack stack = itemHandler.getStackInSlot(0);

        if (!stack.isEmpty() && stack.getCount() > 1) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            return copy;
        }

        return stack;
    }

    public void setItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack = stack.copy();
            stack.setCount(1);
        }
        itemHandler.setStackInSlot(0, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public void clear() {
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));

        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!stack.isEmpty() && stack.getCount() > 1) {
            stack.setCount(1);
            itemHandler.setStackInSlot(0, stack);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        return getItem().isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return itemHandler.getStackInSlot(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        ItemStack stack = itemHandler.getStackInSlot(slot);

        if (!stack.isEmpty() && stack.getCount() > 1) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            return copy;
        }

        return stack;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return itemHandler.extractItem(slot, 1, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return itemHandler.extractItem(slot, 1, false);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (!stack.isEmpty()) {
            stack = stack.copy();
            stack.setCount(1);
        }
        itemHandler.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) return false;
        return player.distanceToSqr(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
        ) <= 64.0D;
    }

    @Override
    public void clearContent() {
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(2);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}