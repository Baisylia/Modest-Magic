package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.common.builtin.smithing.SmithingClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

public class TabletSmithingClientRecipe extends SmithingClientRecipe {

    public TabletSmithingClientRecipe(RecipeHolder<TabletSmithingRecipe> recipeHolder, ItemStack baseStack, List<Holder<Enchantment>> resolvedEnchants) {
		TabletSmithingRecipe recipe = recipeHolder.value();
		var template = SlotContent.of(recipe.template());
		var base = baseStack;

		Identifier baseId = BuiltInRegistries.ITEM.getKey(baseStack.getItem());
		var id = Identifier.fromNamespaceAndPath(
				recipeHolder.id().identifier().getNamespace(),
				"/" + recipeHolder.id().identifier().getPath() + "_rrv/" + baseId.getNamespace() + "/" + baseId.getPath()
		);

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
		var output = outStack;

		super(id, SlotContent.of(base), SlotContent.of(), template, SlotContent.of(output), null,2);

    }
}