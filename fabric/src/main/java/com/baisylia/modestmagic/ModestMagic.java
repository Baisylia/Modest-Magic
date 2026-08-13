package com.baisylia.modestmagic;

import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.item.ModItems;
import com.baisylia.modestmagic.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTabs;

public class ModestMagic implements ModInitializer {

    public static MinecraftServer SERVER = null;

    @Override
    public void onInitialize() {
        CommonClass.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.ENCHANTING_SERIALIZER.get());
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.INFUSING_SERIALIZER.get());
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.SUMMONING_SERIALIZER.get());
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.TABLET_SMITHING_SERIALIZER.get());

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(ModBlocks.ALTAR.get());
            entries.accept(ModBlocks.PEDESTAL.get());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.accept(ModItems.STARDUST.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_AQUATIC.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_CYCLIC.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_EVERLASTING.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_FLINGING.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_GLACIAL.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_HALLOWED.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_HAUNTED.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_HEAVY.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_INFESTED.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_RECOLLECTION.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_SWIFT.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_FIERY.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_LIGHT.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_LUCKY.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_SERRATED.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_PUNCTURING.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_ENDURING.get());
            entries.accept(ModItems.ENCHANTMENT_TABLET_SECURE.get());
        });
    }
}