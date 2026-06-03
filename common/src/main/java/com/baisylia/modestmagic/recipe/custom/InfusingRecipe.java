package com.baisylia.modestmagic.recipe.custom;

import com.baisylia.modestmagic.recipe.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfusingRecipe implements Recipe<RecipeInput> {

    private final Ingredient base;
    private final NonNullList<Ingredient> ingredients;
    private final List<ItemStack> results;

    public InfusingRecipe(Ingredient base, NonNullList<Ingredient> ingredients, List<ItemStack> results) {
        this.base = base;
        this.ingredients = ingredients;
        this.results = results;
    }

    public boolean matches(ItemStack centerItem, List<ItemStack> pedestalItems) {
        if (!base.test(centerItem))
            return false;
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

    public List<ItemStack> getResults() {
        return results;
    }

    @Override
    public boolean matches(@NotNull RecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput input) {
        return results.isEmpty() ? ItemStack.EMPTY : results.getFirst().copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }


    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return ModRecipes.INFUSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipes.INFUSING_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public Ingredient getBase() {
        return base;
    }

    public static class Serializer {

        public static final MapCodec<InfusingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("base").forGetter(r -> r.base),
                Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
                ItemStack.CODEC.listOf().fieldOf("results").forGetter(r -> r.results)
        ).apply(inst, (base, ingredients, results) -> {
            NonNullList<Ingredient> list = NonNullList.create();
            list.addAll(ingredients);
            return new InfusingRecipe(base, list, results);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, InfusingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.results,
                (base, ingredients, results) -> {
                    NonNullList<Ingredient> list = NonNullList.create();
                    list.addAll(ingredients);
                    return new InfusingRecipe(base, list, results);
                }
        );

        public static final RecipeSerializer<InfusingRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}