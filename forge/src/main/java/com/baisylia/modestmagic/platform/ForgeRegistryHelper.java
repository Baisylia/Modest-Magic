package com.baisylia.modestmagic.platform;

import com.baisylia.modestmagic.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ForgeRegistryHelper implements IRegistryHelper {
    @Override
    public <T> IRegistryProvider<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        ForgeRegistryProvider<T> provider = new ForgeRegistryProvider<>(registryKey, modId);
        provider.registerToBus();
        return provider;
    }

    public static class ForgeRegistryProvider<T> implements IRegistryProvider<T> {
        private final DeferredRegister<T> deferredRegister;

        public ForgeRegistryProvider(ResourceKey<? extends Registry<T>> registryKey, String modId) {
            this.deferredRegister = DeferredRegister.create(registryKey, modId);
        }

        public void registerToBus() {
            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        @Override
        public <I extends T> Supplier<I> register(String name, Supplier<? extends I> supplier) {
            return deferredRegister.register(name, supplier);
        }
    }
}