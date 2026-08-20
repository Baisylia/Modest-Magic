package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.platform.Services;
import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

public class TabletSmithingEmiRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final EmiIngredient template;
    private final EmiStack base;
    private final EmiIngredient addition;
    private final EmiStack output;

    public TabletSmithingEmiRecipe(RecipeHolder<TabletSmithingRecipe> recipeHolder, ItemStack baseStack, List<Holder<Enchantment>> resolvedEnchants) {
        TabletSmithingRecipe recipe = recipeHolder.value();
        this.template = EmiIngredient.of(recipe.template());
        this.base = EmiStack.of(baseStack);
        this.addition = EmiIngredient.of(recipe.addition());

        ResourceLocation baseId = BuiltInRegistries.ITEM.getKey(baseStack.getItem());
        this.id = ResourceLocation.fromNamespaceAndPath(
                recipeHolder.id().getNamespace(),
                "/" + recipeHolder.id().getPath() + "_emi/" + baseId.getNamespace() + "/" + baseId.getPath()
        );

        ItemStack outStack = baseStack.copy();
        if (outStack.getItem() == Items.BOOK) {
            outStack = new ItemStack(Items.ENCHANTED_BOOK);
        }

        ItemEnchantments.Mutable map = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (Holder<Enchantment> e : resolvedEnchants) {
            if (Services.PLATFORM.isPrimaryEnchantItem(baseStack, e) || baseStack.is(Items.BOOK)) {
                map.set(e, 1);
            }
        }
        EnchantmentHelper.setEnchantments(outStack, map.toImmutable());
        this.output = EmiStack.of(outStack);
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
        return List.of(template, base, addition);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
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
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(template, 0, 0);
        widgets.addSlot(base, 18, 0);
        widgets.addSlot(addition, 36, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);
        widgets.addSlot(output, 94, 0).recipeContext(this);
    }
}