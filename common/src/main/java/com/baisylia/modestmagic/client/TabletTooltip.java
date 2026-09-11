package com.baisylia.modestmagic.client;

import com.baisylia.modestmagic.config.ModConfig;
import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import com.baisylia.modestmagic.tag.ModTags;
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

import java.util.*;

public class TabletTooltip {

    private static final Map<Item, List<ResourceKey<Enchantment>>> CACHED_ENCHANTMENTS = new HashMap<>();
    private static RecipeManager lastRecipeManager = null;

    public static boolean isTablet(@NotNull ItemStack stack) {
        return stack.is(ModTags.Items.TABLETS);
    }

    private static String toRoman(int number) {
        String[] numerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return (number >= 0 && number <= 10) ? numerals[number] : String.valueOf(number);
    }

    public static void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (!ModConfig.get().showTabletTooltips) return;
        if (!isTablet(stack)) return;
        if (context.registries() == null) return;

        List<ResourceKey<Enchantment>> enchantments = getEnchantments(stack);
        if (enchantments.isEmpty()) return;

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

    public static List<ResourceKey<Enchantment>> getEnchantments(@NotNull ItemStack stack) {
        if (Minecraft.getInstance().level == null) {
            return Collections.emptyList();
        }

        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        if (recipeManager != lastRecipeManager) {
            lastRecipeManager = recipeManager;
            CACHED_ENCHANTMENTS.clear();
        }

        return CACHED_ENCHANTMENTS.computeIfAbsent(stack.getItem(), item -> {
            List<ResourceKey<Enchantment>> enchantments = new ArrayList<>();
            try {
                var recipes = recipeManager.getAllRecipesFor(RecipeType.SMITHING);
                ItemStack testStack = new ItemStack(item);

                for (var recipeHolder : recipes) {
                    if (recipeHolder.value() instanceof TabletSmithingRecipe tabletRecipe) {
                        if (tabletRecipe.template().test(testStack)) {
                            for (ResourceKey<Enchantment> key : tabletRecipe.enchantments()) {
                                if (!enchantments.contains(key)) {
                                    enchantments.add(key);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            return enchantments;
        });
    }

    private static Component getAppliedTo(Enchantment enchantment) {
        Optional<TagKey<Item>> tagOpt = enchantment.getSupportedItems().unwrapKey();
        if (tagOpt.isPresent()) {
            String translationKey = Util.makeDescriptionId("tag", tagOpt.get().location());
            return Component.translatable(translationKey);
        }
        return Component.literal("Various");
    }
}
