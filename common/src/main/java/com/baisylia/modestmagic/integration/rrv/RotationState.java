package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import com.baisylia.modestmagic.config.ModConfig;

public class RotationState {
    public final int cx;
	public final int cy;
	public final int radius;
	public final int total;
	private final ReliableClientRecipe.RecipePosition recipePosition;
	private long pauseOffset = 0;
    private long lastTime = System.currentTimeMillis();

    public RotationState(int cx, int cy, int radius, int total, ReliableClientRecipe.RecipePosition recipePosition) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.total = total;
		this.recipePosition = recipePosition;
	}

    public void update(int mouseX, int mouseY) {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastTime;
        if (deltaTime == 0) return;

        lastTime = currentTime;

        boolean isHovered = false;
        double currentAngle = getAngleWithoutAdvancing();

        for (int i = 0; i < total; i++) {
            double angle = (360.0 / total) * i + currentAngle - 90.0;
            int x = (int) (recipePosition.left()+ cx + Math.cos(Math.toRadians(angle)) * radius) - 9;
            int y = (int) (recipePosition.top()+ cy + Math.sin(Math.toRadians(angle)) * radius) - 9;

            if (mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18) {
                isHovered = true;
                break;
            }
        }

        if (isHovered) {
            pauseOffset += deltaTime;
        }
    }

    private double getAngleWithoutAdvancing() {
        if (ModConfig.get().reducedRrvMotion) {
            return 0.0;
        }
        long activeTime = lastTime - pauseOffset;
        return ((activeTime % 16000L) / 16000.0) * 360.0;
    }

    public double getAngle() {
        return getAngleWithoutAdvancing();
    }
}