package com.baisylia.modestmagic.recipe.custom;

import com.baisylia.modestmagic.recipe.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnchantingRecipe implements Recipe<RecipeInput> {

    private final NonNullList<Ingredient> ingredients;
    private final List<List<ResourceKey<Enchantment>>> enchantmentPools;

    public EnchantingRecipe(NonNullList<Ingredient> ingredients, List<List<ResourceKey<Enchantment>>> enchantmentPools) {
        this.ingredients = ingredients;
        this.enchantmentPools = enchantmentPools;
    }

    public boolean matches(List<ItemStack> pedestalItems) {
        if (pedestalItems.size() != ingredients.size()) return false;
        return matchIngredients(pedestalItems, ingredients, new boolean[pedestalItems.size()], 0);
    }

    private boolean matchIngredients(List<ItemStack> inputs, List<Ingredient> ingredients, boolean[] used, int index) {
        if (index == ingredients.size()) return true;
        Ingredient ing = ingredients.get(index);
        for (int i = 0; i < inputs.size(); i++) {
            if (!used[i] && ing.test(inputs.get(i))) {
                used[i] = true;
                if (matchIngredients(inputs, ingredients, used, index + 1)) return true;
                used[i] = false;
            }
        }
        return false;
    }

    @Override
    public boolean matches(@NotNull RecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput input, @NotNull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.ENCHANTING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.ENCHANTING_TYPE.get();
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<List<ResourceKey<Enchantment>>> getEnchantmentPools() {
        return enchantmentPools;
    }

    public static class Serializer implements RecipeSerializer<EnchantingRecipe> {
        public static final MapCodec<EnchantingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
                ResourceKey.codec(Registries.ENCHANTMENT).listOf().listOf().fieldOf("enchantments").forGetter(r -> r.enchantmentPools)
        ).apply(inst, (ing, pools) -> {
            NonNullList<Ingredient> list = NonNullList.create();
            list.addAll(ing);
            return new EnchantingRecipe(list, pools);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, EnchantingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
                ResourceKey.streamCodec(Registries.ENCHANTMENT).apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()), r -> r.enchantmentPools,
                (ing, pools) -> {
                    NonNullList<Ingredient> list = NonNullList.create();
                    list.addAll(ing);
                    return new EnchantingRecipe(list, pools);
                }
        );

        @Override
        public @NotNull MapCodec<EnchantingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, EnchantingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}