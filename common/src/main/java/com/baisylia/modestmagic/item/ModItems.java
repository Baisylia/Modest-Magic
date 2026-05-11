package com.baisylia.modestmagic.item;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.platform.Services;
import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SimpleFoiledItem;

import java.util.function.Supplier;

public class ModItems {
    public static final IRegistryHelper.IRegistryProvider<Item> ITEMS =
            Services.REGISTRIES.create(Registries.ITEM, Constants.MOD_ID);

    public static final Supplier<Item> STARDUST = ITEMS.register("stardust",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET = ITEMS.register("enchantment_tablet",
            () -> new Item(new Item.Properties()));

    public static final Supplier<Item> ENCHANTMENT_TABLET_AQUATIC = ITEMS.register("enchantment_tablet_aquatic",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_CYCLIC = ITEMS.register("enchantment_tablet_cyclic",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_EVERLASTING = ITEMS.register("enchantment_tablet_everlasting",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_FLINGING = ITEMS.register("enchantment_tablet_flinging",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_GLACIAL = ITEMS.register("enchantment_tablet_glacial",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_HALLOWED = ITEMS.register("enchantment_tablet_hallowed",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_HAUNTED = ITEMS.register("enchantment_tablet_haunted",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_HEAVY = ITEMS.register("enchantment_tablet_heavy",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_INFESTED = ITEMS.register("enchantment_tablet_infested",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_RECOLLECTION = ITEMS.register("enchantment_tablet_recollection",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_SWIFT = ITEMS.register("enchantment_tablet_swift",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_FIERY = ITEMS.register("enchantment_tablet_fiery",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_LIGHT = ITEMS.register("enchantment_tablet_light",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_LUCKY = ITEMS.register("enchantment_tablet_lucky",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_SERRATED = ITEMS.register("enchantment_tablet_serrated",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_PUNCTURING = ITEMS.register("enchantment_tablet_puncturing",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_ENDURING = ITEMS.register("enchantment_tablet_enduring",
            () -> new SimpleFoiledItem(new Item.Properties()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_SECURE = ITEMS.register("enchantment_tablet_secure",
            () -> new SimpleFoiledItem(new Item.Properties()));

    public static void init() {
    }
}