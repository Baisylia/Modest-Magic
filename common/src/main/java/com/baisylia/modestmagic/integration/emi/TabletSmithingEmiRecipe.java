package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabletSmithingEmiRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final EmiIngredient base;
    private final EmiIngredient addition;
    private final List<EmiStack> outputs;

    public TabletSmithingEmiRecipe(TabletSmithingRecipe recipe) {
        this.id = new ResourceLocation(recipe.getId().getNamespace(), "/" + recipe.getId().getPath() + "_emi");
        this.addition = EmiIngredient.of(recipe.getAddition());

        List<EmiStack> validBases = new ArrayList<>();
        List<EmiStack> validOutputs = new ArrayList<>();

        ItemStack[] baseItems = recipe.getBase().getItems();
        List<ItemStack> testStacks = new ArrayList<>();

        if (baseItems.length == 0) {
            for (Item item : ForgeRegistries.ITEMS) {
                testStacks.add(new ItemStack(item));
            }
        } else {
            testStacks.addAll(List.of(baseItems));
        }

        for (ItemStack baseStack : testStacks) {
            boolean isValid = false;

            for (Enchantment e : recipe.getEnchantments()) {
                if (e.canEnchant(baseStack) || baseStack.is(Items.BOOK)) {
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

                Map<Enchantment, Integer> map = new HashMap<>();
                for (Enchantment e : recipe.getEnchantments()) {
                    if (e.canEnchant(baseStack) || baseStack.is(Items.BOOK)) {
                        map.put(e, 1);
                    }
                }
                EnchantmentHelper.setEnchantments(map, outStack);
                validOutputs.add(EmiStack.of(outStack));
            }
        }

        if (validBases.isEmpty()) {
            validBases.add(EmiStack.of(Items.BOOK));
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            Map<Enchantment, Integer> map = new HashMap<>();
            for (Enchantment e : recipe.getEnchantments()) map.put(e, 1);
            EnchantmentHelper.setEnchantments(map, book);
            validOutputs.add(EmiStack.of(book));
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
        return List.of(base, addition);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(base, 0, 0);
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addSlot(addition, 49, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 76, 1);
        widgets.addSlot(EmiIngredient.of(outputs), 107, 0).recipeContext(this);
    }
}