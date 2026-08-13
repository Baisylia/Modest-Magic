package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public abstract class AbstractModestMagicClientRecipe implements ReliableClientRecipe {
	boolean rendered = false;

	@Override
	public void initRecipe() {
		rendered = false;
	}

	public void renderRecipe(RecipeScreenContext context) {
		if (!this.rendered) {
			Screen screen = context.screen();
			if (screen instanceof RecipeViewScreen recipeViewScreen) {
				initRecipe(recipeViewScreen, context.recipePosition(), context.guiGraphics(), context.mouseX(), context.mouseY(), context.partialTicks());
				this.rendered = true;
			}
		}

	}

	public void initRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

	}
}
