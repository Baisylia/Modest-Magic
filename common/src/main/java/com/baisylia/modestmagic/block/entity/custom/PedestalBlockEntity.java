package com.baisylia.modestmagic.block.entity.custom;

import com.baisylia.modestmagic.block.custom.AltarBlock;
import com.baisylia.modestmagic.block.custom.PedestalBlock;
import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.client.ModSounds;
import com.baisylia.modestmagic.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class PedestalBlockEntity extends BlockEntity implements WorldlyContainer {
    private ItemStack inventory = ItemStack.EMPTY;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.PEDESTAL_BLOCK_ENTITY.get(), pos, state);
    }

    protected PedestalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PedestalBlockEntity blockEntity) {
        if (level.isClientSide) return;
        if (!blockEntity.isEmpty()) return;

        if (state.hasProperty(PedestalBlock.AXIS) && state.getValue(PedestalBlock.AXIS) != Direction.Axis.Y) return;
        if (state.hasProperty(PedestalBlock.TOP) && !state.getValue(PedestalBlock.TOP)) return;

        if (ModConfig.get().throwItemsOnPedestals) {
            AABB pickupArea = new AABB(pos.getX(), pos.getY() + 1.0, pos.getZ(),
                    pos.getX() + 1.0, pos.getY() + 1.5, pos.getZ() + 1.0);

            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, pickupArea);

            for (ItemEntity itemEntity : items) {
                ItemStack stack = itemEntity.getItem();
                if (!stack.isEmpty() && itemEntity.isAlive()) {
                    blockEntity.setItem(stack.split(1));

                    if (stack.isEmpty()) {
                        itemEntity.discard();
                    } else {
                        itemEntity.setItem(stack);
                    }

                    SoundEvent sound = state.getBlock() instanceof AltarBlock
                            ? ModSounds.ADD_ITEM_ALTAR.get()
                            : ModSounds.ADD_ITEM_PEDESTAL.get();
                    level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f);

                    break;
                }
            }
        }
    }

    public ItemStack getItem() {
        return this.inventory;
    }

    public void setItem(ItemStack stack) {
        this.inventory = stack;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.setBlock(worldPosition, state.setValue(PedestalBlock.HAS_ITEM, !inventory.isEmpty()), 3);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    public void clear() {
        this.setItem(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.inventory.isEmpty()) {
            tag.put("Inventory", this.inventory.save(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            this.inventory = ItemStack.of(tag.getCompound("Inventory"));
        } else {
            this.inventory = ItemStack.EMPTY;
        }
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return slot == 0 ? this.inventory : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !this.inventory.isEmpty()) {
            ItemStack stack = this.inventory.split(amount);
            if (this.inventory.isEmpty()) clear();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack stack = this.inventory;
            this.inventory = ItemStack.EMPTY;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (slot == 0) setItem(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0D;
    }

    @Override
    public void clearContent() {
        clear();
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction side) {
        return this.inventory.isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
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
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}