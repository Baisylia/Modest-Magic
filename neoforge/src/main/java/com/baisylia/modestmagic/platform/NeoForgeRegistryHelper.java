package com.baisylia.modestmagic.platform;

import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    @Override
    public <T> IRegistryProvider<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        return new NeoForgeRegistryProvider<>(registryKey, modId);
    }

    public static class NeoForgeRegistryProvider<T> implements IRegistryProvider<T> {
        private final Registry<T> registry;
        private final String modId;

        @SuppressWarnings("unchecked")
        public NeoForgeRegistryProvider(ResourceKey<? extends Registry<T>> registryKey, String modId) {
            this.registry = (Registry<T>) BuiltInRegistries.REGISTRY.getValue(registryKey.identifier());
            this.modId = modId;
        }

        @Override
        public <I extends T> Supplier<I> register(String name, Supplier<? extends I> supplier) {
            I registered = Registry.register(registry, Identifier.fromNamespaceAndPath(modId, name), supplier.get());
            return () -> registered;
        }
    }
}