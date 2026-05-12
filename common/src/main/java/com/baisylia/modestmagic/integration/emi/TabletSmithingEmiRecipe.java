package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public class TabletSmithingEmiRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final EmiIngredient template;
    private final EmiIngredient base;
    private final List<EmiStack> outputs;
    private final EmiStack enchantmentIndicator;

    public TabletSmithingEmiRecipe(RecipeHolder<TabletSmithingRecipe> recipeHolder) {
        this.id = ResourceLocation.fromNamespaceAndPath(recipeHolder.id().getNamespace(), "/" + recipeHolder.id().getPath() + "_emi");
        TabletSmithingRecipe recipe = recipeHolder.value();
        this.template = EmiIngredient.of(recipe.template());

        List<EmiStack> validBases = new ArrayList<>();
        List<EmiStack> validOutputs = new ArrayList<>();

        ItemStack[] baseItems = recipe.base().getItems();
        List<ItemStack> testStacks = new ArrayList<>();

        if (baseItems.length == 0) {
            for (Item item : BuiltInRegistries.ITEM) {
                testStacks.add(new ItemStack(item));
            }
        } else {
            testStacks.addAll(List.of(baseItems));
        }

        Registry<Enchantment> enchantRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        List<Holder<Enchantment>> resolvedEnchants = new ArrayList<>();
        for (ResourceKey<Enchantment> key : recipe.enchantments()) {
            enchantRegistry.getHolder(key).ifPresent(resolvedEnchants::add);
        }

        for (ItemStack baseStack : testStacks) {
            boolean isValid = false;

            for (Holder<Enchantment> e : resolvedEnchants) {
                if (e.value().canEnchant(baseStack) || baseStack.is(Items.BOOK)) {
                    isValid = true;
                    break;
                }
            }

            if (isValid) {
                validBases.add(EmiStack.of(baseStack));

                ItemStack outStack = baseStack.copy();
                if (outStack.getItem() == Items.BOOK) {
                    outStack = new ItemStack(Items.ENCHANTED_BOOK);
                }

                ItemEnchantments.Mutable map = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                for (Holder<Enchantment> e : resolvedEnchants) {
                    if (e.value().canEnchant(baseStack) || baseStack.is(Items.BOOK)) {
                        map.set(e, 1);
                    }
                }
                EnchantmentHelper.setEnchantments(outStack, map.toImmutable());
                validOutputs.add(EmiStack.of(outStack));
            }
        }

        ItemStack indicatorBook = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable map = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (Holder<Enchantment> e : resolvedEnchants) {
            map.set(e, 1);
        }
        EnchantmentHelper.setEnchantments(indicatorBook, map.toImmutable());
        this.enchantmentIndicator = EmiStack.of(indicatorBook);

        if (validBases.isEmpty()) {
            validBases.add(EmiStack.of(Items.BOOK));
            validOutputs.add(this.enchantmentIndicator);
        }

        this.base = EmiIngredient.of(validBases);
        this.outputs = validOutputs;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.SMITHING;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(template, base, EmiStack.EMPTY);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 112;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(template, 0, 0);
        widgets.addSlot(base, 18, 0);
        widgets.addSlot(EmiStack.EMPTY, 36, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);
        widgets.addSlot(enchantmentIndicator, 94, 0).recipeContext(this);
    }
}