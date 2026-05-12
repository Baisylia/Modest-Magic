package com.baisylia.modestmagic.recipe.custom;

import com.baisylia.modestmagic.recipe.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfusingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final Ingredient base;
    private final List<ItemStack> results;

    public InfusingRecipe(ResourceLocation id, Ingredient base, NonNullList<Ingredient> ingredients, List<ItemStack> results) {
        this.id = id;
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
    public boolean matches(@NotNull SimpleContainer container, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer container, @NotNull RegistryAccess registryAccess) {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0).copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0);
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.INFUSING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.INFUSING_TYPE.get();
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public Ingredient getBase() {
        return base;
    }

    public static class Serializer implements RecipeSerializer<InfusingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull InfusingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            Ingredient base = Ingredient.fromJson(json.get("base"));
            NonNullList<Ingredient> ingredients = NonNullList.create();

            for (int i = 0; i < ingredientsJson.size(); i++) {
                ingredients.add(Ingredient.fromJson(ingredientsJson.get(i)));
            }

            List<ItemStack> results = new ArrayList<>();
            if (json.has("results")) {
                JsonArray arr = GsonHelper.getAsJsonArray(json, "results");
                for (int i = 0; i < arr.size(); i++) {
                    results.add(ShapedRecipe.itemStackFromJson(arr.get(i).getAsJsonObject()));
                }
            } else if (json.has("result")) {
                results.add(ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
            }

            return new InfusingRecipe(id, base, ingredients, results);
        }

        @Override
        public @NotNull InfusingRecipe fromNetwork(@NotNull ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);

            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buf));
            }

            Ingredient base = Ingredient.fromNetwork(buf);

            int resultsSize = buf.readVarInt();
            List<ItemStack> results = new ArrayList<>();
            for (int i = 0; i < resultsSize; i++) {
                results.add(buf.readItem());
            }

            return new InfusingRecipe(id, base, ingredients, results);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, InfusingRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());

            for (Ingredient ing : recipe.ingredients) {
                ing.toNetwork(buf);
            }

            recipe.base.toNetwork(buf);

            buf.writeVarInt(recipe.results.size());
            for (ItemStack stack : recipe.results) {
                buf.writeItem(stack);
            }
        }
    }
}