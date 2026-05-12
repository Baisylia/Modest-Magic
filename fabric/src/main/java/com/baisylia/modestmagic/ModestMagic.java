package com.baisylia.modestmagic;

import com.baisylia.modestmagic.block.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FuelRegistry;

public class ModestMagic implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        ModBlocks.FUEL_ITEMS.forEach((item, time) -> FuelRegistry.INSTANCE.add(item.get(), time));
    }
}