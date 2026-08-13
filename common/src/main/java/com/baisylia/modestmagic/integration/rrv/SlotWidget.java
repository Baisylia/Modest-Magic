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

	private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
	private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");

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

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX > x && mouseX < x + 16 && mouseY > y && mouseY < y + 16;
	}

	void renderItem(GuiGraphicsExtractor graphics, int mouseX, int mouseY, ItemStack current, int x, int y) {
		if (isMouseOver(mouseX, mouseY)) {
			Minecraft mc = Minecraft.getInstance();
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, x - 4, y - 4, 24, 24);
			graphics.setComponentTooltipForNextFrame(mc.font, Screen.getTooltipFromItem(mc, current), mouseX, mouseY);
		}
		graphics.fakeItem(current, x, y);
		if (isMouseOver(mouseX, mouseY)) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, x - 4, y - 4, 24, 24);
		}
	}

	@Override
	public Bounds getBounds() {
		return new Bounds(x,y,18,18);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor draw, int mouseX, int mouseY, float delta) {
		draw.fakeItem(stack, x, y);
	}
}
