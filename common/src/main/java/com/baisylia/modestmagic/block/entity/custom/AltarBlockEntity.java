package com.baisylia.modestmagic.block.entity.custom;

import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.client.ModSounds;
import com.baisylia.modestmagic.recipe.ModRecipes;
import com.baisylia.modestmagic.recipe.custom.EnchantingRecipe;
import com.baisylia.modestmagic.recipe.custom.InfusingRecipe;
import com.baisylia.modestmagic.recipe.custom.SummoningRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltarBlockEntity extends PedestalBlockEntity {

    private static final int PEDESTAL_RANGE = 3;

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), pos, state);
    }

    public static void spawnItemEntity(Level level, ItemStack stack, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDeltaMovement(xMotion, yMotion, zMotion);
        level.addFreshEntity(entity);
    }

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
                if (!recipe.getResults().isEmpty()) {
                    // Do Thingy
                    spawnItemEntity(this.level, this.getItem().getCraftingRemainingItem(),
                            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.25, this.worldPosition.getZ() + 0.5,
                            0, 0, 0);
                    
                    // Select a random result from the outputs
                    ItemStack result = recipe.getResults().get(level.random.nextInt(recipe.getResults().size()));
                    this.setItem(result.copy());

                    enchantEffects(pedestals, ParticleTypes.FLAME, ModSounds.ALTAR_ENCHANT.get());
                    return true;
                }
            }
        }

        // Enchanting Recipe
        for (EnchantingRecipe recipe : level.getRecipeManager().getAllRecipesFor(ModRecipes.ENCHANTING_TYPE.get())) {
            if (recipe.matches(items)) {
                if (!recipe.getEnchantmentPools().isEmpty()) {
                    Map<Enchantment, Integer> existing = EnchantmentHelper.getEnchantments(this.getItem());
                    boolean appliedAny = false;

                    // Select a random pool of enchantments
                    List<Enchantment> pool = recipe.getEnchantmentPools().get(level.random.nextInt(recipe.getEnchantmentPools().size()));

                    for (Enchantment enchantment : pool) {
                        if (!enchantment.canEnchant(this.getItem())) continue;

                        boolean incompatible = existing.keySet().stream().anyMatch(e -> e != enchantment && !e.isCompatibleWith(enchantment));
                        if (incompatible) continue;

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
        }

        // Summoning Recipe
        for (SummoningRecipe recipe : level.getRecipeManager().getAllRecipesFor(ModRecipes.SUMMONING_TYPE.get())) {
            if (recipe.matches(this.getItem(), items)) {
                if (!recipe.getOutcomes().isEmpty()) {
                    // Do Thingy
                    if (level instanceof ServerLevel server) {
                        SummoningRecipe.SummonOutcome outcome = recipe.getOutcomes().get(level.random.nextInt(recipe.getOutcomes().size()));
                        var entity = outcome.entity().create(server);
                        if (entity != null) {
                            if (!outcome.nbt().isEmpty()) {
                                CompoundTag nbt = outcome.nbt().copy();
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
                        spawnItemEntity(this.level, this.getItem().getCraftingRemainingItem(),
                                this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.25, this.worldPosition.getZ() + 0.5,
                                0, 0, 0);
                        this.clearContent();
                    } else {
                        int damage = recipe.getDurabilityCost();
                        if (damage > 0 && stack.isDamageableItem()) {
                            if (stack.hurt(damage, level.random, null)) {
                                spawnItemEntity(this.level, this.getItem().getCraftingRemainingItem(),
                                        this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.25, this.worldPosition.getZ() + 0.5,
                                        0, 0, 0);
                                this.clearContent();
                            } else {
                                spawnItemEntity(this.level, this.getItem().getCraftingRemainingItem(),
                                        this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.25, this.worldPosition.getZ() + 0.5,
                                        0, 0, 0);
                                this.setItem(stack);
                            }
                        }
                    }

                    enchantEffects(pedestals, ParticleTypes.AMBIENT_ENTITY_EFFECT, ModSounds.ALTAR_SUMMON.get());
                    return true;
                }
            }
        }
        return false;
    }

    public <T extends ParticleOptions> void enchantEffects(List<PedestalBlockEntity> pedestals, T particle, SoundEvent soundEvent) {
        // Eat Ingredients Nyum Nyum Nyum
        for (PedestalBlockEntity pedestal : pedestals) {
            spawnItemEntity(pedestal.getLevel(), pedestal.getItem().getCraftingRemainingItem(),
                    pedestal.getBlockPos().getX() + 0.5, pedestal.getBlockPos().getY() + 1.25, pedestal.getBlockPos().getZ() + 0.5,
                    0, 0, 0);
            pedestal.clear();
        }
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
                    0.0, 0.0, 0.0,
                    0.05
            );
            serverLevel.sendParticles(
                    ParticleTypes.ENCHANT,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 2,
                    worldPosition.getZ() + 0.5,
                    35,
                    0.0, 0.0, 0.0,
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
