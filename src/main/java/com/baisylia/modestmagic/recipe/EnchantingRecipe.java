package com.baisylia.modestmagic.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class EnchantingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final List<Enchantment> enchantments;

    public EnchantingRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, List<Enchantment> enchantments) {
        this.id = id;
        this.ingredients = ingredients;
        this.enchantments = enchantments;
    }

    public boolean matches(List<ItemStack> pedestalItems) {
        NonNullList<ItemStack> inputs = NonNullList.create();
        inputs.addAll(pedestalItems);
        return net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, ingredients) != null;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) { return false; }

    @Override
    public ItemStack assemble(SimpleContainer container) { return ItemStack.EMPTY; }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public ItemStack getResultItem() { return ItemStack.EMPTY; }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() { return ModRecipes.ENCHANTING_SERIALIZER.get(); }

    @Override
    public RecipeType<?> getType() { return ModRecipes.ENCHANTING_TYPE.get(); }

    public NonNullList<Ingredient> getIngredients() { return ingredients; }

    public List<Enchantment> getEnchantments() { return enchantments; }

    public static class Serializer implements RecipeSerializer<EnchantingRecipe> {

        @Override
        public EnchantingRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < ingredientsJson.size(); i++)
                ingredients.add(Ingredient.fromJson(ingredientsJson.get(i)));

            JsonArray enchantsJson = GsonHelper.getAsJsonArray(json, "enchantments");
            List<Enchantment> enchantments = new ArrayList<>();
            for (int i = 0; i < enchantsJson.size(); i++) {
                Enchantment e = ForgeRegistries.ENCHANTMENTS.getValue(
                        new ResourceLocation(enchantsJson.get(i).getAsString())
                );
                if (e != null) enchantments.add(e);
            }

            return new EnchantingRecipe(id, ingredients, enchantments);
        }

        @Override
        public EnchantingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) ingredients.set(i, Ingredient.fromNetwork(buf));

            int enchSize = buf.readVarInt();
            List<Enchantment> enchantments = new ArrayList<>();
            for (int i = 0; i < enchSize; i++)
                enchantments.add(ForgeRegistries.ENCHANTMENTS.getValue(buf.readResourceLocation()));

            return new EnchantingRecipe(id, ingredients, enchantments);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, EnchantingRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) ing.toNetwork(buf);

            buf.writeVarInt(recipe.enchantments.size());
            for (Enchantment e : recipe.enchantments)
                buf.writeResourceLocation(ForgeRegistries.ENCHANTMENTS.getKey(e));
        }
    }
}