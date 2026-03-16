package com.baisylia.modestmagic.item;

import com.baisylia.modestmagic.ModestMagic;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModestMagic.MOD_ID);


    public static final RegistryObject<Item> ENCHANTMENT_TABLET = ITEMS.register("enchantment_tablet",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
    public static final RegistryObject<Item> ENCHANTMENT_TABLET_AQUATIC = ITEMS.register("enchantment_tablet_aquatic",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
