package com.baisylia.modestmagic.recipe;

import com.baisylia.modestmagic.ModestMagic;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ModestMagic.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModestMagic.MOD_ID);



    public static final RegistryObject<RecipeType<InfusingRecipe>> INFUSING_TYPE =
            TYPES.register("infusing", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<InfusingRecipe>> INFUSING_SERIALIZER =
            SERIALIZERS.register("infusing", () -> InfusingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<EnchantingRecipe>> ENCHANTING_TYPE =
            TYPES.register("enchanting", () -> new RecipeType<>() {});

    public static final RegistryObject<RecipeSerializer<EnchantingRecipe>> ENCHANTING_SERIALIZER =
            SERIALIZERS.register("enchanting", EnchantingRecipe.Serializer::new);



    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
        SERIALIZERS.register(eventBus);
    }
}
