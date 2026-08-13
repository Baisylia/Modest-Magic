package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.recipe.ModRecipes;
import com.baisylia.modestmagic.recipe.custom.EnchantingRecipe;
import com.baisylia.modestmagic.recipe.custom.InfusingRecipe;
import com.baisylia.modestmagic.recipe.custom.SummoningRecipe;
import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class ModestMagicRrvPlugin implements ReliableRecipeViewerClientPlugin {

    /**
     * Consolidates a list of SlotContents, combining identical items and summing their amounts.
     */
    public static List<SlotContent> consolidateItems(List<SlotContent> inputs) {
        List<SlotContent> uniqueIngredients = new ArrayList<>();
        List<Integer> amounts = new ArrayList<>();

        for (SlotContent ing : inputs) {
            if (ing.getValidContents().isEmpty()) continue;

            boolean found = false;
            for (int i = 0; i < uniqueIngredients.size(); i++) {
                SlotContent existing = uniqueIngredients.get(i);
                // compare the first stack to see if they are the same ingredient requirement
                if (!existing.getValidContents().isEmpty() && existing.getValidContents().getFirst() == (ing.getValidContents().getFirst())) {
                    amounts.set(i, amounts.get(i) + 1);
                    found = true;
                    break;
                }
            }

            if (!found) {
                uniqueIngredients.add(ing);
                amounts.add(1);
            }
        }

        // rebuild the SlotContents with the summed amounts
        List<SlotContent> consolidated = new ArrayList<>();
        for (int i = 0; i < uniqueIngredients.size(); i++) {
            int amount = amounts.get(i);
            List<ItemStack> newStacks = new ArrayList<>();
            for (ItemStack s : uniqueIngredients.get(i).getValidContents()) {
				ItemStack copy = s.copy();
                copy.setCount(amount);
                newStacks.add(copy);
            }
            consolidated.add(SlotContent.of(newStacks));
        }

        return consolidated;
    }

	@Override
	public void onIntegrationInitialize() {

		ItemView.addClientRecipeProvider(recipes->{

		for (RecipeHolder<InfusingRecipe> holder : ClientRecipeManager.INSTANCE.getRecipesForType(ModRecipes.INFUSING_TYPE.get())) {
			recipes.add(new InfusingClientRecipe(holder));
		}

		for (RecipeHolder<EnchantingRecipe> holder : ClientRecipeManager.INSTANCE.getRecipesForType(ModRecipes.ENCHANTING_TYPE.get())) {
			recipes.add(new EnchantingClientRecipe(holder));
		}

		for (RecipeHolder<SummoningRecipe> holder : ClientRecipeManager.INSTANCE.getRecipesForType(ModRecipes.SUMMONING_TYPE.get())) {
			recipes.add(new SummoningClientRecipe(holder));
		}

		for (RecipeHolder<?> holder : ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMITHING)) {
			if (holder.value() instanceof TabletSmithingRecipe) {

				@SuppressWarnings("unchecked")
				RecipeHolder<TabletSmithingRecipe> typedHolder = (RecipeHolder<TabletSmithingRecipe>) holder;
				TabletSmithingRecipe recipe = typedHolder.value();
				Registry<Enchantment> enchantRegistry;
				if (Minecraft.getInstance().level != null) {
					enchantRegistry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
				} else {
					enchantRegistry = null;
				}

				if (enchantRegistry == null) {
					System.out.println("ENCHANTMENTS NOT PRESENT");
					continue;
				}

				List<ItemStack> baseItems = SlotContent.of(recipe.base()).getValidContents();
				List<ItemStack> testStacks = new ArrayList<>();

				if (baseItems.isEmpty()) {
					for (Item item : BuiltInRegistries.ITEM) {
						testStacks.add(item.getDefaultInstance());
					}
				} else {
					testStacks.addAll((baseItems));
				}

				List<Holder<Enchantment>> resolvedEnchants = new ArrayList<>();
				for (ResourceKey<Enchantment> key : recipe.enchantments()) {
					enchantRegistry.get(key).ifPresent(resolvedEnchants::add);
				}

				boolean addedAny = false;
				for (ItemStack baseStack : testStacks) {
					boolean isValid = false;

					for (Holder<Enchantment> e : resolvedEnchants) {
						if (e.value().canEnchant(baseStack) || baseStack.is(Items.BOOK)) {
							isValid = true;
							break;
						}
					}

					if (isValid) {
						recipes.add(new TabletSmithingClientRecipe(typedHolder, baseStack, resolvedEnchants));
						addedAny = true;
					}
				}

				if (!addedAny) {
					recipes.add(new TabletSmithingClientRecipe(typedHolder, new ItemStack(Items.BOOK), resolvedEnchants));
				}
			}
		}
		});
	}
}