package com.baisylia.modestmagic.platform;

import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    public static final List<DeferredRegister<?>> REGISTERS = new ArrayList<>();

    public static void register(IEventBus eventBus) {
        for (DeferredRegister<?> register : REGISTERS) {
            register.register(eventBus);
        }
    }

    @Override
    public <T> IRegistryProvider<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        ForgeRegistryProvider<T> provider = new ForgeRegistryProvider<>(registryKey, modId);
        REGISTERS.add(provider.deferredRegister);
        return provider;
    }

    public static class ForgeRegistryProvider<T> implements IRegistryProvider<T> {
        final DeferredRegister<T> deferredRegister;

        public ForgeRegistryProvider(ResourceKey<? extends Registry<T>> registryKey, String modId) {
            this.deferredRegister = DeferredRegister.create(registryKey, modId);
        }

        @Override
        public <I extends T> Supplier<I> register(String name, Supplier<? extends I> supplier) {
            return deferredRegister.register(name, supplier);
        }
    }
}