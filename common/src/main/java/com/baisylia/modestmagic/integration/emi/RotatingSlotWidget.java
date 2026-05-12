package com.baisylia.modestmagic.integration.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.gui.GuiGraphics;

public class RotatingSlotWidget extends SlotWidget {
    private final RotationState state;
    private final EmiIngredient ingredient;
    private final int index;

    public RotatingSlotWidget(RotationState state, EmiIngredient ingredient, int index) {
        super(ingredient, 0, 0);
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        state.update(mouseX, mouseY);

        double exactX = getExactDoubleX();
        double exactY = getExactDoubleY();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(exactX + 1, exactY + 1, 0);

        ingredient.render(guiGraphics, 0, 0, delta);

        guiGraphics.pose().popPose();
    }
}