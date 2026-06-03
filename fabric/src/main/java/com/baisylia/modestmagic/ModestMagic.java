package com.baisylia.modestmagic;

import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class ModestMagic implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        FuelValueEvents.BUILD.register((builder, context)->{
            ModBlocks.FUEL_ITEMS.forEach((item, time) -> builder.add(item.get(), time));
        });

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