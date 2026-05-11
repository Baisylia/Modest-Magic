package com.baisylia.modestmagic.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public interface IRegistryHelper {
    <T> IRegistryProvider<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId);

    interface IRegistryProvider<T> {
        <I extends T> Supplier<I> register(String name, Supplier<? extends I> supplier);
    }
}