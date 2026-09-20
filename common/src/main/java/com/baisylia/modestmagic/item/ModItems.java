package com.baisylia.modestmagic.item;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.platform.Services;
import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModItems {
    public static final IRegistryHelper.IRegistryProvider<Item> ITEMS =
            Services.REGISTRIES.create(Registries.ITEM, Constants.MOD_ID);

    public static final Supplier<Item> STARDUST = registerItem("stardust",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET = registerItem("enchantment_tablet",
            Item::new, (new Item.Properties()));

    public static final Supplier<Item> ENCHANTMENT_TABLET_AQUATIC = registerItem("enchantment_tablet_aquatic",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_CYCLIC = registerItem("enchantment_tablet_cyclic",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_EVERLASTING = registerItem("enchantment_tablet_everlasting",
            Item::new, (foiledProps()));

    public static final Supplier<Item> ENCHANTMENT_TABLET_FLINGING = registerItem("enchantment_tablet_flinging",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_GLACIAL = registerItem("enchantment_tablet_glacial",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_HALLOWED = registerItem("enchantment_tablet_hallowed",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_HAUNTED = registerItem("enchantment_tablet_haunted",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_HEAVY = registerItem("enchantment_tablet_heavy",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_INFESTED = registerItem("enchantment_tablet_infested",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_RECOLLECTION = registerItem("enchantment_tablet_recollection",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_SWIFT = registerItem("enchantment_tablet_swift",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_FIERY = registerItem("enchantment_tablet_fiery",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_LIGHT = registerItem("enchantment_tablet_light",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_LUCKY = registerItem("enchantment_tablet_lucky",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_SERRATED = registerItem("enchantment_tablet_serrated",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_PUNCTURING = registerItem("enchantment_tablet_puncturing",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_ENDURING = registerItem("enchantment_tablet_enduring",
            Item::new, (foiledProps()));
    public static final Supplier<Item> ENCHANTMENT_TABLET_SECURE = registerItem("enchantment_tablet_secure",
            Item::new, (foiledProps()));

    private static Item.Properties foiledProps() {
        return new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));

        // Create the item instance.
        T item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return ()-> item;
    }

    public static void init() {
    }
}