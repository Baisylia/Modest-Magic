package com.baisylia.modestmagic.recipe.custom;

import com.baisylia.modestmagic.recipe.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SummoningRecipe implements Recipe<RecipeInput> {

    private final Ingredient base;
    private final NonNullList<Ingredient> ingredients;
    private final List<SummonOutcome> outcomes;
    private final boolean consumeBase;
    private final int durabilityCost;

    public SummoningRecipe(Ingredient base, NonNullList<Ingredient> ingredients, List<SummonOutcome> outcomes, boolean consumeBase, int durabilityCost) {
        this.base = base;
        this.ingredients = ingredients;
        this.outcomes = outcomes;
        this.consumeBase = consumeBase;
        this.durabilityCost = durabilityCost;
    }

    public List<SummonOutcome> getOutcomes() {
        return outcomes;
    }

    public int getDurabilityCost() {
        return durabilityCost;
    }

    public boolean shouldConsumeBase() {
        return consumeBase;
    }

    public boolean matches(ItemStack centerItem, List<ItemStack> pedestalItems) {
        if (!base.test(centerItem)) return false;
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
    public boolean matches(@NotNull RecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return ModRecipes.SUMMONING_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipes.SUMMONING_TYPE.get();
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

    public record SummonOutcome(EntityType<?> entity, CompoundTag nbt) {
    }

    public static class Serializer {

        public static final Codec<SummonOutcome> OUTCOME_CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("result_entity").forGetter(SummonOutcome::entity),
                CompoundTag.CODEC.optionalFieldOf("entity_nbt", new CompoundTag()).forGetter(SummonOutcome::nbt)
        ).apply(inst, SummonOutcome::new));

        public static final MapCodec<SummoningRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("base").forGetter(r -> r.base),
                Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
                OUTCOME_CODEC.listOf().fieldOf("outcomes").forGetter(r -> r.outcomes),
                Codec.BOOL.optionalFieldOf("consume_base", true).forGetter(r -> r.consumeBase),
                Codec.INT.optionalFieldOf("durability_taken", 0).forGetter(r -> r.durabilityCost)
        ).apply(inst, (base, ingredients, outcomes, consume, durability) -> {
            NonNullList<Ingredient> list = NonNullList.create();
            list.addAll(ingredients);
            return new SummoningRecipe(base, list, outcomes, consume, durability);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, SummonOutcome> OUTCOME_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ENTITY_TYPE), SummonOutcome::entity,
                ByteBufCodecs.COMPOUND_TAG, SummonOutcome::nbt,
                SummonOutcome::new
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SummoningRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
                OUTCOME_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.outcomes,
                ByteBufCodecs.BOOL, r -> r.consumeBase,
                ByteBufCodecs.INT, r -> r.durabilityCost,
                (base, ingredients, outcomes, consume, durability) -> {
                    NonNullList<Ingredient> list = NonNullList.create();
                    list.addAll(ingredients);
                    return new SummoningRecipe(base, list, outcomes, consume, durability);
                }
        );

        public static final RecipeSerializer<SummoningRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}