package com.baisylia.modestmagic.recipe.custom;

import com.baisylia.modestmagic.recipe.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class SummoningRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final Ingredient base;
    private final EntityType<?> resultEntity;
    private final boolean consumeBase;

    public SummoningRecipe(ResourceLocation id, Ingredient base, NonNullList<Ingredient> ingredients, EntityType<?> resultEntity, boolean consumeBase) {
        this.id = id;
        this.base = base;
        this.ingredients = ingredients;
        this.resultEntity = resultEntity;
        this.consumeBase = consumeBase;
    }

    public boolean shouldConsumeBase() {
        return consumeBase;
    }

    public boolean matches(ItemStack centerItem, List<ItemStack> pedestalItems) {
        if(!base.test(centerItem))
            return false;

        List<ItemStack> inputs = new ArrayList<>(pedestalItems);
        return net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, ingredients) != null;
    }

    public EntityType<?> getResultEntity() {
        return resultEntity;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SUMMONING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SUMMONING_TYPE.get();
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public Ingredient getBase() {
        return base;
    }

    public static class Serializer implements RecipeSerializer<SummoningRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public SummoningRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            Ingredient base = Ingredient.fromJson(json.get("base"));
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < ingredientsJson.size(); i++) {
                ingredients.add(Ingredient.fromJson(ingredientsJson.get(i)));
            }
            boolean consumeBase = GsonHelper.getAsBoolean(json, "consume_base", true);
            EntityType<?> entity = ForgeRegistries.ENTITY_TYPES.getValue(
                    new ResourceLocation(GsonHelper.getAsString(json, "result_entity"))
            );

            return new SummoningRecipe(id, base, ingredients, entity, consumeBase);
        }

        @Override
        public SummoningRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) ingredients.set(i, Ingredient.fromNetwork(buf));
            boolean consumeBase = buf.readBoolean();
            Ingredient base = Ingredient.fromNetwork(buf);
            EntityType<?> entity = ForgeRegistries.ENTITY_TYPES.getValue(buf.readResourceLocation());

            return new SummoningRecipe(id, base, ingredients, entity, consumeBase);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SummoningRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) ing.toNetwork(buf);
            buf.writeBoolean(recipe.consumeBase);
            recipe.base.toNetwork(buf);
            buf.writeResourceLocation(ForgeRegistries.ENTITY_TYPES.getKey(recipe.resultEntity));
        }
    }
}