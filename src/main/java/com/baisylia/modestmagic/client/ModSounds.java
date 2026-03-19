package com.baisylia.modestmagic.client;

import com.baisylia.modestmagic.ModestMagic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;


public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModestMagic.MOD_ID);

    public static final RegistryObject<SoundEvent> ADD_ITEM_ALTAR = SOUND_EVENTS.register("add_item_altar",
            () -> new SoundEvent(new ResourceLocation(ModestMagic.MOD_ID, "block.altar.add_item")));

    public static final RegistryObject<SoundEvent> ADD_ITEM_PEDESTAL = SOUND_EVENTS.register("add_item_pedestal",
            () -> new SoundEvent(new ResourceLocation(ModestMagic.MOD_ID, "block.pedestal.add_item")));

    public static final RegistryObject<SoundEvent> REMOVE_ITEM_PEDESTAL = SOUND_EVENTS.register("remove_item_pedestal",
            () -> new SoundEvent(new ResourceLocation(ModestMagic.MOD_ID, "block.pedestal.remove_item")));

    public static final RegistryObject<SoundEvent> ALTAR_ENCHANT = SOUND_EVENTS.register("altar_enchant",
            () -> new SoundEvent(new ResourceLocation(ModestMagic.MOD_ID, "block.altar.enchant")));

    public static final RegistryObject<SoundEvent> ALTAR_SUMMON = SOUND_EVENTS.register("altar_summon",
            () -> new SoundEvent(new ResourceLocation(ModestMagic.MOD_ID, "block.altar.summon")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
