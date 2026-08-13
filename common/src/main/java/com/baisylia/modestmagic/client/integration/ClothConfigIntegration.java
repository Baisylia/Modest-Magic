package com.baisylia.modestmagic.client.integration;

import com.baisylia.modestmagic.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigIntegration {

    public static Screen createScreen(Screen parent) {
        ModConfig config = ModConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.modestmagic.title"));

        builder.setSavingRunnable(ModConfig::save);

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.modestmagic.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.modestmagic.option.throwItemsOnPedestals"), config.throwItemsOnPedestals)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.throwItemsOnPedestals = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.modestmagic.option.reducedEmiMotion"), config.reducedRrvMotion)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.reducedRrvMotion = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.modestmagic.option.showTabletTooltips"), config.showTabletTooltips)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.showTabletTooltips = newValue)
                .build());

        return builder.build();
    }
}