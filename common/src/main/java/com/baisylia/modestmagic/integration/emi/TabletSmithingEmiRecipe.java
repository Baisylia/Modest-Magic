package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TabletSmithingEmiRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final EmiIngredient template;
    private final EmiStack base;
    private final EmiStack output;

    public TabletSmithingEmiRecipe(ResourceLocation recipeId, TabletSmithingRecipe recipe, ItemStack baseStack, List<Enchantment> resolvedEnchants) {
        this.template = EmiIngredient.of(recipe.getTemplate());
        this.base = EmiStack.of(baseStack);

        ResourceLocation baseId = BuiltInRegistries.ITEM.getKey(baseStack.getItem());
        this.id = new ResourceLocation(
                recipeId.getNamespace(),
                "/" + recipeId.getPath() + "_emi/" + baseId.getNamespace() + "/" + baseId.getPath()
        );

        ItemStack outStack = baseStack.copy();
        if (outStack.getItem() == Items.BOOK) {
            outStack = new ItemStack(Items.ENCHANTED_BOOK);
        }

        Map<Enchantment, Integer> map = new LinkedHashMap<>();
        for (Enchantment e : resolvedEnchants) {
            if (e.canEnchant(baseStack) || baseStack.is(Items.BOOK)) {
                map.put(e, 1);
            }
        }

        EnchantmentHelper.setEnchantments(map, outStack);
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
        return List.of(template, base, EmiStack.EMPTY);
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
        widgets.addSlot(EmiStack.EMPTY, 36, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);
        widgets.addSlot(output, 94, 0).recipeContext(this);
    }
}