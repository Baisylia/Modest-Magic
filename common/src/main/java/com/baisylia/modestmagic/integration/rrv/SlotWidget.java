package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public class SlotWidget extends Widget {
	private final SlotContent ingredient;
	private final int x;
	private final int y;

	public SlotWidget(SlotContent ingredient, int x, int y, ReliableClientRecipe.RecipePosition recipePosition) {
		this.ingredient = ingredient;
		this.x = recipePosition.left() + x;
		this.y = recipePosition.top() + y;
	}

	@Override
	public Bounds getBounds() {
		return new Bounds(x,y,18,18);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor draw, int mouseX, int mouseY, float delta) {
		ItemStack stack = ingredient.getValidContents().getFirst();
		draw.fakeItem(stack, x, y);
	}
}
