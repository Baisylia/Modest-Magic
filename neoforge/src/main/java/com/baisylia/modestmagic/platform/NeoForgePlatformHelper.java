package com.baisylia.modestmagic.platform;

import com.baisylia.modestmagic.ModestMagic;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;
import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import com.baisylia.modestmagic.events.ModestMagicClientEvents;
import com.baisylia.modestmagic.platform.services.IPlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getCurrent().getDist() == Dist.CLIENT;
    }

    @Override
    public RecipeMap getSynchronizedRecipeMap() {
        return ModestMagic.MAP;
    }

    @Override
    public BlockEntityType<AltarBlockEntity> createAltar() {
        return new BlockEntityType<>(AltarBlockEntity::new, ModBlocks.ALTAR.get());
    }

    @Override
    public BlockEntityType<PedestalBlockEntity> createPedestal() {
        return new BlockEntityType<>(PedestalBlockEntity::new, ModBlocks.PEDESTAL.get());
    }

    @Override
    public HolderGetter.Provider registryAccess() {
        if (ModestMagic.SERVER != null) {
            return ModestMagic.SERVER.registryAccess();
        } else {
            return ModestMagicClientEvents.getRegistryAccess();
        }
    }

    @Override
    public boolean isPrimaryEnchantItem(ItemStack stack, Holder<Enchantment> enchantment) {
        return stack.isPrimaryItemFor(enchantment);
    }
}