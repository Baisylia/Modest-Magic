package com.baisylia.modestmagic.client;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.platform.Services;
import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class ModSounds {
    public static final IRegistryHelper.IRegistryProvider<SoundEvent> SOUND_EVENTS =
            Services.REGISTRIES.create(Registries.SOUND_EVENT, Constants.MOD_ID);

    public static final Supplier<SoundEvent> ADD_ITEM_ALTAR = SOUND_EVENTS.register("add_item_altar",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block.altar.add_item")));

    public static final Supplier<SoundEvent> ADD_ITEM_PEDESTAL = SOUND_EVENTS.register("add_item_pedestal",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block.pedestal.add_item")));

    public static final Supplier<SoundEvent> REMOVE_ITEM_PEDESTAL = SOUND_EVENTS.register("remove_item_pedestal",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block.pedestal.remove_item")));

    public static final Supplier<SoundEvent> ALTAR_ENCHANT = SOUND_EVENTS.register("altar_enchant",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block.altar.enchant")));

    public static final Supplier<SoundEvent> ALTAR_SUMMON = SOUND_EVENTS.register("altar_summon",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block.altar.summon")));

    public static void init() {
    }
}