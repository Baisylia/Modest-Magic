package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.baisylia.modestmagic.config.ModConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

public class HoveringSlotWidget extends SlotWidget {
    private final int indexOffset;
    private final SlotContent ingredient;

    public HoveringSlotWidget(SlotContent ingredient, int x, int y, int indexOffset, ReliableClientRecipe.RecipePosition recipePosition) {
        super(ingredient, x, y, recipePosition);
        this.ingredient = ingredient;
        this.indexOffset = indexOffset;
        this.drawBack(false);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        float hover = 0f;

        if (!ModConfig.get().reducedRrvMotion) {
            hover = (float) Math.sin((System.currentTimeMillis() % 4000L) / 4000.0f * Math.PI * 2 + indexOffset) * 2.0f;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0, hover);
        guiGraphics.fakeItem(this.ingredient.current(), this.getBounds().x(), this.getBounds().y());
        guiGraphics.pose().popMatrix();
    }
}