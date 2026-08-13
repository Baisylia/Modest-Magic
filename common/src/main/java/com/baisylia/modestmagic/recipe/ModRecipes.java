package com.baisylia.modestmagic.recipe;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.platform.Services;
import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import com.baisylia.modestmagic.recipe.custom.EnchantingRecipe;
import com.baisylia.modestmagic.recipe.custom.InfusingRecipe;
import com.baisylia.modestmagic.recipe.custom.SummoningRecipe;
import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class ModRecipes {

    public static final IRegistryHelper.IRegistryProvider<RecipeType<?>> TYPES =
            Services.REGISTRIES.create(Registries.RECIPE_TYPE, Constants.MOD_ID);

    public static final IRegistryHelper.IRegistryProvider<RecipeSerializer<?>> SERIALIZERS =
            Services.REGISTRIES.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);

    public static final Supplier<RecipeType<InfusingRecipe>> INFUSING_TYPE =
            TYPES.register("infusing", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "infusing";
                }
            });

    public static final Supplier<RecipeSerializer<InfusingRecipe>> INFUSING_SERIALIZER =
            SERIALIZERS.register("infusing", () -> InfusingRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeType<EnchantingRecipe>> ENCHANTING_TYPE =
            TYPES.register("enchanting", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "enchanting";
                }
            });
    public static final Supplier<RecipeSerializer<EnchantingRecipe>> ENCHANTING_SERIALIZER =
            SERIALIZERS.register("enchanting", ()-> new RecipeSerializer<>(EnchantingRecipe.Serializer.CODEC, EnchantingRecipe.Serializer.STREAM_CODEC));

    public static final Supplier<RecipeType<SummoningRecipe>> SUMMONING_TYPE =
            TYPES.register("summoning", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "summoning";
                }
            });

    public static final Supplier<RecipeSerializer<SummoningRecipe>> SUMMONING_SERIALIZER =
            SERIALIZERS.register("summoning", () -> SummoningRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeSerializer<TabletSmithingRecipe>> TABLET_SMITHING_SERIALIZER =
            SERIALIZERS.register("tablet_smithing", () -> TabletSmithingRecipe.Serializer.INSTANCE);

    public static void init() {
    }
}