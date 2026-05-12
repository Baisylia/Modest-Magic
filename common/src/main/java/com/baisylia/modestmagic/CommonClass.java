package com.baisylia.modestmagic;

import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.client.ModSounds;
import com.baisylia.modestmagic.config.ModConfig;
import com.baisylia.modestmagic.item.ModItems;
import com.baisylia.modestmagic.recipe.ModRecipes;

public class CommonClass {
    public static void init() {
        ModConfig.load();
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModSounds.init();
        ModRecipes.init();
    }
}