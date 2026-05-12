package com.baisylia.modestmagic.recipe.custom;

import com.baisylia.modestmagic.recipe.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchantingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final List<List<Enchantment>> enchantmentPools;

    public EnchantingRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, List<List<Enchantment>> enchantmentPools) {
        this.id = id;
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
    public boolean matches(@NotNull SimpleContainer container, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer container, @NotNull RegistryAccess registryAccess) {
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
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
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

    public List<List<Enchantment>> getEnchantmentPools() {
        return enchantmentPools;
    }

    public static class Serializer implements RecipeSerializer<EnchantingRecipe> {

        @Override
        public @NotNull EnchantingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < ingredientsJson.size(); i++)
                ingredients.add(Ingredient.fromJson(ingredientsJson.get(i)));

            JsonArray enchantsJson = GsonHelper.getAsJsonArray(json, "enchantments");
            List<List<Enchantment>> enchantmentPools = new ArrayList<>();

            for (int i = 0; i < enchantsJson.size(); i++) {
                List<Enchantment> pool = new ArrayList<>();
                if (enchantsJson.get(i).isJsonArray()) {
                    JsonArray group = enchantsJson.get(i).getAsJsonArray();
                    for (int j = 0; j < group.size(); j++) {
                        Enchantment e = BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(group.get(j).getAsString()));
                        if (e != null) pool.add(e);
                    }
                } else {
                    Enchantment e = BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(enchantsJson.get(i).getAsString()));
                    if (e != null) pool.add(e);
                }
                if (!pool.isEmpty()) enchantmentPools.add(pool);
            }

            return new EnchantingRecipe(id, ingredients, enchantmentPools);
        }

        @Override
        public @NotNull EnchantingRecipe fromNetwork(@NotNull ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) ingredients.set(i, Ingredient.fromNetwork(buf));

            int poolsSize = buf.readVarInt();
            List<List<Enchantment>> enchantmentPools = new ArrayList<>();
            for (int i = 0; i < poolsSize; i++) {
                int poolSize = buf.readVarInt();
                List<Enchantment> pool = new ArrayList<>();
                for (int j = 0; j < poolSize; j++) {
                    pool.add(BuiltInRegistries.ENCHANTMENT.get(buf.readResourceLocation()));
                }
                enchantmentPools.add(pool);
            }

            return new EnchantingRecipe(id, ingredients, enchantmentPools);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, EnchantingRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) ing.toNetwork(buf);

            buf.writeVarInt(recipe.enchantmentPools.size());
            for (List<Enchantment> pool : recipe.enchantmentPools) {
                buf.writeVarInt(pool.size());
                for (Enchantment e : pool) {
                    buf.writeResourceLocation(BuiltInRegistries.ENCHANTMENT.getKey(e));
                }
            }
        }
    }
}