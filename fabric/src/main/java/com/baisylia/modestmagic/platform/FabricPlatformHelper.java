package com.baisylia.modestmagic.platform;

import com.baisylia.modestmagic.ModestMagic;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;
import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import com.baisylia.modestmagic.client.ModestMagicClient;
import com.baisylia.modestmagic.platform.services.IPlatformHelper;
import com.google.common.collect.ImmutableMultimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public ImmutableMultimap<RecipeType<?>, RecipeHolder<?>> getSynchronizedRecipeMap() {
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType = ImmutableMultimap.builder();

        for(RecipeHolder<?> recipe : Minecraft.getInstance().level.recipeAccess().getSynchronizedRecipes().recipes()) {
            byType.put(recipe.value().getType(), recipe);
        }

        return byType.build();
    }

    @Override
    public BlockEntityType<AltarBlockEntity> createAltar() {
        return FabricBlockEntityTypeBuilder.create(AltarBlockEntity::new, ModBlocks.ALTAR.get()).build();
    }

    @Override
    public BlockEntityType<PedestalBlockEntity> createPedestal() {
        return FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new, ModBlocks.PEDESTAL.get()).build();
    }

    @Override
    public HolderGetter.Provider registryAccess() {
        if (ModestMagic.SERVER != null) {
            return ModestMagic.SERVER.registryAccess();
        } else {
            return ModestMagicClient.getRegistryAccess();
        }
    }

    public boolean isPrimaryEnchantItem(ItemStack stack, Holder<Enchantment> enchantment) {
        return stack.getItem().canBeEnchantedWith(stack, enchantment, EnchantingContext.PRIMARY);
    }
}