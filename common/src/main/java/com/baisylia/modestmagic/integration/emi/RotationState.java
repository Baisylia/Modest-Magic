package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.config.ModestMagicConfig;

public class RotationState {
    public final int cx, cy, radius, total;
    private long pauseOffset = 0;
    private long lastTime = System.currentTimeMillis();

    public RotationState(int cx, int cy, int radius, int total) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.total = total;
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
            int x = (int) (cx + Math.cos(Math.toRadians(angle)) * radius) - 9;
            int y = (int) (cy + Math.sin(Math.toRadians(angle)) * radius) - 9;

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
        if (ModestMagicConfig.REDUCED_EMI_MOTION.get()) {
            return 0.0;
        }
        long activeTime = lastTime - pauseOffset;
        return ((activeTime % 16000L) / 16000.0) * 360.0;
    }

    public double getAngle() {
        return getAngleWithoutAdvancing();
    }
}