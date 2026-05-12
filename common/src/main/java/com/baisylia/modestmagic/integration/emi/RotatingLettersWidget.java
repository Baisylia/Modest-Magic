package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.config.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RotatingLettersWidget extends Widget {
    private final ResourceLocation texture;
    private final int cx, cy, radius;
    private final int numLetters = 12;
    private final int letterSize = 8;

    public RotatingLettersWidget(ResourceLocation texture, int cx, int cy, int radius) {
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
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();

        double baseAngle = 0.0;

        if (!ModConfig.get().reducedEmiMotion) {
            baseAngle = -((System.currentTimeMillis() % 24000L) / 24000.0) * 360.0;
        }

        for (int i = 0; i < numLetters; i++) {
            double angle = baseAngle + (360.0 / numLetters) * i;

            double exactX = cx + Math.cos(Math.toRadians(angle)) * radius - (letterSize / 2.0);
            double exactY = cy + Math.sin(Math.toRadians(angle)) * radius - (letterSize / 2.0);

            int u = i * letterSize;
            int v = 0;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(exactX, exactY, 0);

            guiGraphics.blit(texture, 0, 0, u, v, letterSize, letterSize, letterSize * numLetters, letterSize);

            guiGraphics.pose().popPose();
        }
    }
}