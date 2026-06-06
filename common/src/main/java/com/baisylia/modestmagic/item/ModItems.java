package com.baisylia.modestmagic.item;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.item.custom.TabletItem;
import com.baisylia.modestmagic.platform.Services;
import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ModItems {
    public static final IRegistryHelper.IRegistryProvider<Item> ITEMS =
            Services.REGISTRIES.create(Registries.ITEM, Constants.MOD_ID);

    public static final Supplier<Item> STARDUST = ITEMS.register("stardust",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET = ITEMS.register("enchantment_tablet",
            () -> new Item(new Item.Properties()));

    public static final Supplier<Item> ENCHANTMENT_TABLET_AQUATIC = ITEMS.register("enchantment_tablet_aquatic",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_CYCLIC = ITEMS.register("enchantment_tablet_cyclic",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_EVERLASTING = ITEMS.register("enchantment_tablet_everlasting",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_FLINGING = ITEMS.register("enchantment_tablet_flinging",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_GLACIAL = ITEMS.register("enchantment_tablet_glacial",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_HALLOWED = ITEMS.register("enchantment_tablet_hallowed",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_HAUNTED = ITEMS.register("enchantment_tablet_haunted",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_HEAVY = ITEMS.register("enchantment_tablet_heavy",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_INFESTED = ITEMS.register("enchantment_tablet_infested",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_RECOLLECTION = ITEMS.register("enchantment_tablet_recollection",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_SWIFT = ITEMS.register("enchantment_tablet_swift",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_FIERY = ITEMS.register("enchantment_tablet_fiery",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_LIGHT = ITEMS.register("enchantment_tablet_light",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_LUCKY = ITEMS.register("enchantment_tablet_lucky",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_SERRATED = ITEMS.register("enchantment_tablet_serrated",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_PUNCTURING = ITEMS.register("enchantment_tablet_puncturing",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_ENDURING = ITEMS.register("enchantment_tablet_enduring",
            () -> new TabletItem(foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_SECURE = ITEMS.register("enchantment_tablet_secure",
            () -> new TabletItem(foiledProps()));

    private static Item.Properties foiledProps() {
        return new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    public static void init() {
    }
}