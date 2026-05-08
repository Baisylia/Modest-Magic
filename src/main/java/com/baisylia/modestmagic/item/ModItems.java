package com.baisylia.modestmagic.item;

import com.baisylia.modestmagic.ModestMagic;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModestMagic.MOD_ID);

    public static final RegistryObject<Item> STARDUST = ITEMS.register("stardust",
            () -> new SimpleFoiledItem(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
    public static final RegistryObject<Item> ENCHANTMENT_TABLET = ITEMS.register("enchantment_tablet",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_AQUATIC = ITEMS.register("enchantment_tablet_aquatic",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_CYCLIC = ITEMS.register("enchantment_tablet_cyclic",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_EVERLASTING = ITEMS.register("enchantment_tablet_everlasting",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_FLINGING = ITEMS.register("enchantment_tablet_flinging",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_GLACIAL = ITEMS.register("enchantment_tablet_glacial",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_HALLOWED = ITEMS.register("enchantment_tablet_hallowed",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_HAUNTED = ITEMS.register("enchantment_tablet_haunted",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_HEAVY = ITEMS.register("enchantment_tablet_heavy",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_INFESTED = ITEMS.register("enchantment_tablet_infested",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_RECOLLECTION = ITEMS.register("enchantment_tablet_recollection",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_SWIFT = ITEMS.register("enchantment_tablet_swift",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));


    public static final RegistryObject<Item> ENCHANTMENT_TABLET_FIERY = ITEMS.register("enchantment_tablet_fiery",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_LIGHT = ITEMS.register("enchantment_tablet_light",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_LUCKY = ITEMS.register("enchantment_tablet_lucky",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_SERRATED = ITEMS.register("enchantment_tablet_serrated",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_PUNCTURING = ITEMS.register("enchantment_tablet_puncturing",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_ENDURING = ITEMS.register("enchantment_tablet_enduring",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));

    public static final RegistryObject<Item> ENCHANTMENT_TABLET_SECURE = ITEMS.register("enchantment_tablet_secure",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
