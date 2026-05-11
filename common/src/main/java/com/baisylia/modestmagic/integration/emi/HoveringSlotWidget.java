package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.config.ModestMagicConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;

public class HoveringSlotWidget extends SlotWidget {
    private final int indexOffset;

    public HoveringSlotWidget(EmiIngredient ingredient, int x, int y, int indexOffset) {
        super(ingredient, x, y);
        this.indexOffset = indexOffset;
        this.drawBack(false);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        float hover = 0f;

        if (!ModestMagicConfig.REDUCED_EMI_MOTION.get()) {
            hover = (float) Math.sin((System.currentTimeMillis() % 4000L) / 4000.0f * Math.PI * 2 + indexOffset) * 2.0f;
        }

        poseStack.pushPose();
        poseStack.translate(0, hover, 0);
        super.render(poseStack, mouseX, mouseY - (int) hover, delta);
        poseStack.popPose();
    }
}