package com.baisylia.modestmagic.integration.emi;

import com.baisylia.modestmagic.config.ModConfig;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class HoveringSlotWidget extends SlotWidget {
    private final int indexOffset;
    private final EmiIngredient ingredient;

    public HoveringSlotWidget(EmiIngredient ingredient, int x, int y, int indexOffset) {
        super(ingredient, x, y);
        this.ingredient = ingredient;
        this.indexOffset = indexOffset;
        this.drawBack(false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        float hover = 0f;

        if (!ModConfig.get().reducedEmiMotion) {
            hover = (float) Math.sin((System.currentTimeMillis() % 4000L) / 4000.0f * Math.PI * 2 + indexOffset) * 2.0f;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, hover, 0);
        this.ingredient.render(guiGraphics, this.getBounds().x(), this.getBounds().y(), delta);
        guiGraphics.pose().popPose();
    }
}