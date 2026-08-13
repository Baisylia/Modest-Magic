package com.baisylia.modestmagic.platform.services;

import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;
import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.nio.file.Path;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Gets the configuration directory for the current platform.
     *
     * @return The path to the config directory.
     */
    Path getConfigDirectory();

    /**
     * Checks if the code is running on the physical client.
     *
     * @return True if on the client, false if on a dedicated server.
     */
    boolean isPhysicalClient();

    RecipeMap getSynchronizedRecipeMap();

    BlockEntityType<AltarBlockEntity> createAltar();

    BlockEntityType<PedestalBlockEntity> createPedestal();

	HolderGetter.Provider registryAccess();
    /**
     * Checks whether an item is a "primary" candidate for an enchantment.
     *
     * @param stack       The item stack to check.
     * @param enchantment The enchantment to check.
     * @return True if the item should be considered a primary candidate for the enchantment.
     */
    boolean isPrimaryEnchantItem(ItemStack stack, Holder<Enchantment> enchantment);
}