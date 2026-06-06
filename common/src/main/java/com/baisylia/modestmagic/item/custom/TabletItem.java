package com.baisylia.modestmagic.item.custom;

import com.baisylia.modestmagic.config.ModConfig;
import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabletItem extends Item {
    private List<ResourceKey<Enchantment>> cachedEnchantments = null;

    public TabletItem(Properties properties) {
        super(properties);
    }

    /**
     * Helper to convert integer levels into Roman Numerals.
     */
    private static String toRoman(int number) {
        String[] numerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return (number >= 0 && number <= 10) ? numerals[number] : String.valueOf(number);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (!ModConfig.get().showTabletTooltips) return;

        if (context.registries() == null) return;

        List<ResourceKey<Enchantment>> enchantments = getEnchantments();
        if (enchantments == null || enchantments.isEmpty()) return;

        var registry = context.registries().lookupOrThrow(Registries.ENCHANTMENT);
        boolean isShiftDown = Screen.hasShiftDown();

        for (ResourceKey<Enchantment> key : enchantments) {
            Optional<Holder.Reference<Enchantment>> optEnchant = registry.get(key);
            if (optEnchant.isEmpty()) continue;

            Enchantment enchantment = optEnchant.get().value();
            MutableComponent name = Component.translatable(Util.makeDescriptionId("enchantment", key.location()));

            if (!isShiftDown) {
                tooltip.add(name.withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(name.withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.UNDERLINE));

                Component maxLevelValue = Component.literal(toRoman(enchantment.getMaxLevel())).withStyle(ChatFormatting.GRAY);
                tooltip.add(Component.literal("Max. Level: ").withStyle(ChatFormatting.DARK_GRAY).append(maxLevelValue));

                Component appliedToValue = getAppliedTo(enchantment).copy().withStyle(ChatFormatting.GRAY);
                tooltip.add(Component.literal("Applied to: ").withStyle(ChatFormatting.DARK_GRAY).append(appliedToValue));

                String namespace = key.location().getNamespace();
                String path = key.location().getPath();
                String primaryDescKey = "enchantment." + namespace + "." + path + ".description";
                String fallbackDescKey = "enchantment." + namespace + "." + path + ".desc";

                String finalDescKey = I18n.exists(primaryDescKey) ? primaryDescKey : fallbackDescKey;
                tooltip.add(Component.translatable(finalDescKey).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    /**
     * Fetches the enchantments from the RecipeManager based on this specific Tablet item.
     */
    private List<ResourceKey<Enchantment>> getEnchantments() {
        if (cachedEnchantments != null) {
            return cachedEnchantments;
        }

        cachedEnchantments = new ArrayList<>();

        try {
            if (Minecraft.getInstance().level != null) {
                RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

                var recipes = recipeManager.getAllRecipesFor(RecipeType.SMITHING);
                ItemStack thisStack = new ItemStack(this);

                for (var recipeHolder : recipes) {
                    if (recipeHolder.value() instanceof TabletSmithingRecipe tabletRecipe) {
                        if (tabletRecipe.template().test(thisStack)) {
                            cachedEnchantments.addAll(tabletRecipe.enchantments());
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return cachedEnchantments;
    }

    /**
     * Converts the item tag into its localized component.
     */
    private Component getAppliedTo(Enchantment enchantment) {
        Optional<TagKey<Item>> tagOpt = enchantment.getSupportedItems().unwrapKey();
        if (tagOpt.isPresent()) {
            String translationKey = Util.makeDescriptionId("tag", tagOpt.get().location());
            return Component.translatable(translationKey);
        }
        return Component.literal("Various");
    }
}