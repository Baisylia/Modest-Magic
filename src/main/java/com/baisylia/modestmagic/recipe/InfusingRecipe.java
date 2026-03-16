package com.baisylia.modestmagic.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

public class InfusingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final Ingredient base;
    private final ItemStack result;

    public InfusingRecipe(ResourceLocation id, Ingredient base, NonNullList<Ingredient> ingredients, ItemStack result) {
        this.id = id;
        this.base = base;
        this.ingredients = ingredients;
        this.result = result;
    }

    public boolean matches(ItemStack centerItem, List<ItemStack> pedestalItems) {

        if(!base.test(centerItem))
            return false;

        List<ItemStack> inputs = new ArrayList<>(pedestalItems);

        return RecipeMatcher.findMatches(inputs, ingredients) != null;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return false; // unused
    }

    @Override
    public ItemStack assemble(SimpleContainer container) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.INFUSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.INFUSING_TYPE.get();
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public static class Serializer implements RecipeSerializer<InfusingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public InfusingRecipe fromJson(ResourceLocation id, JsonObject json) {

            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            Ingredient base = Ingredient.fromJson(json.get("base"));
            NonNullList<Ingredient> ingredients = NonNullList.create();

            for(int i = 0; i < ingredientsJson.size(); i++) {
                ingredients.add(Ingredient.fromJson(ingredientsJson.get(i)));
            }

            ItemStack result = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(json, "result")
            );

            return new InfusingRecipe(id, base, ingredients, result);
        }

        @Override
        public InfusingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {

            int size = buf.readVarInt();

            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);

            for(int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buf));
            }

            Ingredient base = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();

            return new InfusingRecipe(id, base, ingredients, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, InfusingRecipe recipe) {

            buf.writeVarInt(recipe.ingredients.size());

            for(Ingredient ing : recipe.ingredients) {
                ing.toNetwork(buf);
            }

            recipe.base.toNetwork(buf);
            buf.writeItem(recipe.result);
        }
    }
}