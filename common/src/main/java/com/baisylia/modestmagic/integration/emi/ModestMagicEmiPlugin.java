package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.recipe.ModRecipes;
import com.baisylia.modestmagic.recipe.custom.EnchantingRecipe;
import com.baisylia.modestmagic.recipe.custom.InfusingRecipe;
import com.baisylia.modestmagic.recipe.custom.SummoningRecipe;
import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

@EmiEntrypoint
public class ModestMagicEmiPlugin implements EmiPlugin {

    public static final EmiRecipeCategory INFUSING = new EmiRecipeCategory(
            new ResourceLocation(Constants.MOD_ID, "infusing"),
            EmiStack.of(ModBlocks.ALTAR.get())
    );

    public static final EmiRecipeCategory ENCHANTING = new EmiRecipeCategory(
            new ResourceLocation(Constants.MOD_ID, "enchanting"),
            EmiStack.of(ModBlocks.ALTAR.get())
    );

    public static final EmiRecipeCategory SUMMONING = new EmiRecipeCategory(
            new ResourceLocation(Constants.MOD_ID, "summoning"),
            EmiStack.of(ModBlocks.ALTAR.get())
    );

    public static List<EmiIngredient> consolidateItems(List<EmiIngredient> inputs) {
        List<EmiIngredient> uniqueIngredients = new ArrayList<>();
        List<Long> amounts = new ArrayList<>();

        for (EmiIngredient ing : inputs) {
            if (ing.getEmiStacks().isEmpty()) continue;

            boolean found = false;
            for (int i = 0; i < uniqueIngredients.size(); i++) {
                EmiIngredient existing = uniqueIngredients.get(i);
                if (!existing.getEmiStacks().isEmpty() && existing.getEmiStacks().get(0).isEqual(ing.getEmiStacks().get(0))) {
                    amounts.set(i, amounts.get(i) + ing.getAmount());
                    found = true;
                    break;
                }
            }

            if (!found) {
                uniqueIngredients.add(ing);
                amounts.add(ing.getAmount());
            }
        }

        List<EmiIngredient> consolidated = new ArrayList<>();
        for (int i = 0; i < uniqueIngredients.size(); i++) {
            long amount = amounts.get(i);
            List<EmiStack> newStacks = new ArrayList<>();
            for (EmiStack s : uniqueIngredients.get(i).getEmiStacks()) {
                EmiStack copy = s.copy();
                copy.setAmount(amount);
                newStacks.add(copy);
            }
            consolidated.add(EmiIngredient.of(newStacks));
        }

        return consolidated;
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(INFUSING);
        registry.addCategory(ENCHANTING);
        registry.addCategory(SUMMONING);

        registry.addWorkstation(INFUSING, EmiStack.of(ModBlocks.ALTAR.get()));
        registry.addWorkstation(ENCHANTING, EmiStack.of(ModBlocks.ALTAR.get()));
        registry.addWorkstation(SUMMONING, EmiStack.of(ModBlocks.ALTAR.get()));

        for (InfusingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.INFUSING_TYPE.get())) {
            registry.addRecipe(new InfusingEmiRecipe(recipe));
        }

        for (EnchantingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.ENCHANTING_TYPE.get())) {
            registry.addRecipe(new EnchantingEmiRecipe(recipe));
        }

        for (SummoningRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.SUMMONING_TYPE.get())) {
            registry.addRecipe(new SummoningEmiRecipe(recipe));
        }

        for (Recipe<?> genericRecipe : registry.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING)) {
            if (genericRecipe instanceof TabletSmithingRecipe recipe) {
                ResourceLocation recipeId = recipe.getId();
                registry.removeRecipes(recipeId);

                ItemStack[] baseItems = recipe.getBase().getItems();
                List<ItemStack> testStacks = new ArrayList<>();

                if (baseItems.length == 0) {
                    for (Item item : BuiltInRegistries.ITEM) {
                        testStacks.add(item.getDefaultInstance());
                    }
                } else {
                    testStacks.addAll(List.of(baseItems));
                }

                boolean addedAny = false;
                for (ItemStack baseStack : testStacks) {
                    boolean isValid = false;

                    for (Enchantment e : recipe.getEnchantments()) {
                        if (e.canEnchant(baseStack) || baseStack.is(Items.BOOK)) {
                            isValid = true;
                            break;
                        }
                    }

                    if (isValid) {
                        registry.addRecipe(new TabletSmithingEmiRecipe(recipeId, recipe, baseStack, recipe.getEnchantments()));
                        addedAny = true;
                    }
                }

                if (!addedAny) {
                    registry.addRecipe(new TabletSmithingEmiRecipe(recipeId, recipe, new ItemStack(Items.BOOK), recipe.getEnchantments()));
                }
            }
        }
    }
}
