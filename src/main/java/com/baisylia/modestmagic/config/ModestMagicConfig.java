package com.baisylia.modestmagic.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModestMagicConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue THROW_ITEMS_ON_PEDESTALS;
    public static final ForgeConfigSpec.BooleanValue REDUCED_EMI_MOTION;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("general");
        THROW_ITEMS_ON_PEDESTALS = builder
                .comment("If true, dropped items will automatically be picked up by empty pedestals.")
                .define("throwItemsOnPedestals", true);
        builder.pop();

        builder.push("client");
        REDUCED_EMI_MOTION = builder
                .comment("If true, disables hovering and rotating animations in EMI recipes.")
                .define("reducedEmiMotion", false);
        builder.pop();

        SPEC = builder.build();
    }
}