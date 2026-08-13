package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.util.RRVInputUtil;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotWidget extends Widget {
	private final SlotContent ingredient;
	private final int x;
	private final int y;
	private ItemStack stack;

	public SlotWidget(SlotContent ingredient, int x, int y, ReliableClientRecipe.RecipePosition recipePosition) {
		this.ingredient = ingredient;
		this.x = recipePosition.left() + x;
		this.y = recipePosition.top() + y;
		this.stack = ingredient.getValidContents().getFirst();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (this.stack != null) {


			if (RRVInputUtil.isLeftClick(event)) {
				ItemViewOverlay.INSTANCE.openRecipeView(this.stack, ActionType.RESULT);
				return true;
			}

			if (RRVInputUtil.isRightClick(event)) {
				ItemViewOverlay.INSTANCE.openRecipeView(this.stack, ActionType.INPUT);
				return true;
			}
		}

		return super.mouseClicked(event, doubleClick);
	}

	void renderItem(GuiGraphicsExtractor graphics, int mouseX, int mouseY, ItemStack current, int x, int y) {
		if (isMouseOver(mouseX, mouseY)) {
			Minecraft mc = Minecraft.getInstance();
			graphics.setComponentTooltipForNextFrame(mc.font, Screen.getTooltipFromItem(mc, current), mouseX, mouseY);
		}
		graphics.fakeItem(current, x, y);
		graphics.itemDecorations(Minecraft.getInstance().font, current, x, y);
	}

	@Override
	public Bounds getBounds() {
		return new Bounds(x,y,18,18);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor draw, int mouseX, int mouseY, float delta) {
		renderItem(draw, mouseX, mouseY, stack, x, y);
	}
}
