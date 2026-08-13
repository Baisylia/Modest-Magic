package com.baisylia.modestmagic.integration.rrv;

import com.baisylia.modestmagic.config.ModConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class RotatingLettersWidget extends Widget {
    private final Identifier texture;
    private final int cx, cy, radius;
    private final int numLetters = 12;
    private final int letterSize = 8;

    public RotatingLettersWidget(Identifier texture, int cx, int cy, int radius) {
        this.texture = texture;
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(cx - radius - letterSize, cy - radius - letterSize, (radius + letterSize) * 2, (radius + letterSize) * 2);
    }

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
//        RenderSystem.setShaderTexture(0, texture);
//        RenderSystem.enableBlend();

        double baseAngle = 0.0;

        if (!ModConfig.get().reducedRrvMotion) {
            baseAngle = -((System.currentTimeMillis() % 24000L) / 24000.0) * 360.0;
        }

        for (int i = 0; i < numLetters; i++) {
            double angle = baseAngle + (360.0 / numLetters) * i;

            double exactX = cx + Math.cos(Math.toRadians(angle)) * radius - (letterSize / 2.0);
            double exactY = cy + Math.sin(Math.toRadians(angle)) * radius - (letterSize / 2.0);

            int u = i * letterSize;
            int v = 0;

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate((float) exactX, (float) exactY);

            guiGraphics.blit(texture, 0, 0, u, v, letterSize, letterSize, letterSize * numLetters, letterSize);

            guiGraphics.pose().popMatrix();
        }
    }
}