package com.baisylia.modestmagic.block.entity.custom;

import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.client.ModSounds;
import com.baisylia.modestmagic.recipe.custom.InfusingRecipe;
import com.baisylia.modestmagic.recipe.custom.EnchantingRecipe;
import com.baisylia.modestmagic.recipe.ModRecipes;
import com.baisylia.modestmagic.recipe.custom.SummoningRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltarBlockEntity extends PedestalBlockEntity {

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), pos, state);
    }

    private static final int PEDESTAL_RANGE = 3;

    public boolean tryCraft() {
        if (level == null || level.isClientSide)
            return false;

        List<PedestalBlockEntity> pedestals = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();

        // Scan Pedestals
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-PEDESTAL_RANGE, -PEDESTAL_RANGE, -PEDESTAL_RANGE),
                worldPosition.offset(PEDESTAL_RANGE, PEDESTAL_RANGE, PEDESTAL_RANGE))) {
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof PedestalBlockEntity pedestal && be != this) {
                ItemStack stack = pedestal.getItem();
                if (!stack.isEmpty()) {
                    pedestals.add(pedestal);
                    items.add(stack);
                }
            }
        }

        if (items.isEmpty())
            return false;

        // Infusing Recipe
        for (InfusingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ModRecipes.INFUSING_TYPE.get())) {
            if (recipe.matches(this.getItem(), items)) {
                // Do Thingy
                this.setItem(recipe.getResult());

                enchantEffects(pedestals, ParticleTypes.FLAME, ModSounds.ALTAR_ENCHANT.get());
                return true;
            }
        }

        // Enchanting Recipe
        for (EnchantingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ModRecipes.ENCHANTING_TYPE.get())) {
            if (recipe.matches(items)) {

                // Do Thingy
                Map<Enchantment, Integer> existing = EnchantmentHelper.getEnchantments(this.getItem());
                boolean appliedAny = false;

                for (Enchantment enchantment : recipe.getEnchantments()) {
                    // Incompatibilities
                    if (!enchantment.canEnchant(this.getItem())) continue;

                    boolean incompatible = existing.keySet().stream().anyMatch(e -> e != enchantment && !e.isCompatibleWith(enchantment));
                    if (incompatible) continue;

                    // Increment Level
                    int currentLevel = existing.getOrDefault(enchantment, 0);
                    int newLevel = Math.min(currentLevel + 1, enchantment.getMaxLevel());

                    if (newLevel > currentLevel) {
                        existing.put(enchantment, newLevel);
                        EnchantmentHelper.setEnchantments(existing, this.getItem());
                        appliedAny = true;
                    }
                }
                if (!appliedAny) return false;

                enchantEffects(pedestals, ParticleTypes.SOUL_FIRE_FLAME, ModSounds.ALTAR_ENCHANT.get());
                return true;
            }
        }

        // Summoning Recipe
        for (SummoningRecipe recipe : level.getRecipeManager().getAllRecipesFor(ModRecipes.SUMMONING_TYPE.get())) {
            if (recipe.matches(this.getItem(), items)) {
                // Do Thingy
                if (level instanceof ServerLevel server) {
                    var entity = recipe.getResultEntity().create(server);
                    if (entity != null) {
                        if (!recipe.getEntityNbt().isEmpty()) {
                            CompoundTag nbt = recipe.getEntityNbt().copy();
                            nbt.remove("Pos");
                            nbt.remove("Motion");
                            nbt.remove("Rotation");
                            entity.load(nbt);
                        }
                        entity.moveTo(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, server.random.nextFloat() * 360F, 0);
                        server.addFreshEntity(entity);
                    }
                }

                ItemStack stack = this.getItem();
                if (recipe.shouldConsumeBase()) {
                    this.clearContent();
                }
                else {
                    int damage = recipe.getDurabilityCost();
                    if (damage > 0 && stack.isDamageableItem()) {
                        if (stack.hurt(damage, level.random, null)) {
                            this.clearContent();
                        } else {
                            this.setItem(stack);
                        }
                    }
                }

                enchantEffects(pedestals, ParticleTypes.AMBIENT_ENTITY_EFFECT, ModSounds.ALTAR_SUMMON.get());
                return true;
            }
        }
        return false;
    }
    public <T extends ParticleOptions> void enchantEffects(List<PedestalBlockEntity> pedestals, T particle, @NotNull SoundEvent soundEvent) {
        for (PedestalBlockEntity pedestal : pedestals) pedestal.clear();
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

        if (level instanceof ServerLevel serverLevel) {
            // Sound
            serverLevel.playSound(null, worldPosition, soundEvent,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);

            // Particles
            makeParticles(serverLevel, worldPosition, particle);
            for (PedestalBlockEntity pedestal : pedestals) {
                BlockPos pPos = pedestal.getBlockPos();
                makeParticles(serverLevel, pPos, particle);
            }
            serverLevel.sendParticles(
                    ParticleTypes.WITCH,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.0,
                    worldPosition.getZ() + 0.5,
                    20,
                    0.0,0.0,0.0,
                    0.05
            );
            serverLevel.sendParticles(
                    ParticleTypes.ENCHANT,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 2,
                    worldPosition.getZ() + 0.5,
                    35,
                    0.0,0.0,0.0,
                    3.0
            );
        }
    }
    public <T extends ParticleOptions> void makeParticles(ServerLevel serverLevel, BlockPos pos, T particle) {
        serverLevel.sendParticles(
                particle,
                pos.getX() + 0.5,
                pos.getY() + 1.5,
                pos.getZ() + 0.5,
                10,
                0.0, 0.0, 0.0,
                0.05
        );
    }
}
