package com.baisylia.modestmagic.platform;

import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public <T> IRegistryProvider<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        return new FabricRegistryProvider<>(registryKey, modId);
    }

    public static class FabricRegistryProvider<T> implements IRegistryProvider<T> {
        private final Registry<T> registry;
        private final String modId;

        @SuppressWarnings("unchecked")
        public FabricRegistryProvider(ResourceKey<? extends Registry<T>> registryKey, String modId) {
            this.registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location());
            this.modId = modId;
        }

        @Override
        public <I extends T> Supplier<I> register(String name, Supplier<? extends I> supplier) {
            I registered = Registry.register(registry, new ResourceLocation(modId, name), supplier.get());
            return () -> registered;
        }
    }
}