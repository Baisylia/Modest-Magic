package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RotatingSlotWidget extends SlotWidget {
    private final RotationState state;
    private final SlotContent ingredient;
    private final int index;

	public RotatingSlotWidget(RotationState state, SlotContent ingredient, int index, ReliableClientRecipe.RecipePosition recipePosition) {
        super(ingredient, 0, 0, recipePosition);
        this.state = state;
        this.ingredient = ingredient;
        this.index = index;
		this.drawBack(false);
    }

    private double getExactAngle() {
        return (360.0 / state.total) * index + state.getAngle() - 90.0;
    }

    private double getExactDoubleX() {
        return state.cx + Math.cos(Math.toRadians(getExactAngle())) * state.radius - 9;
    }

    private double getExactDoubleY() {
        return state.cy + Math.sin(Math.toRadians(getExactAngle())) * state.radius - 9;
    }

    @Override
    public Bounds getBounds() {
        return new Bounds((int) getExactDoubleX(), (int) getExactDoubleY(), 18, 18);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        state.update(mouseX, mouseY);

        double exactX = getExactDoubleX();
        double exactY = getExactDoubleY();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate((float) (exactX + 1), (float) (exactY + 1));

        guiGraphics.fakeItem(ingredient.getValidContents().getFirst(), super.getBounds().x(), super.getBounds().y());

        guiGraphics.pose().popMatrix();
    }
}
