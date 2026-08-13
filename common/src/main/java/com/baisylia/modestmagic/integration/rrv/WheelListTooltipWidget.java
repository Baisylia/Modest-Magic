package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WheelListTooltipWidget extends Widget {
    private final int cx, cy, triggerRadius;
    private final List<SlotContent> allItems;

    public WheelListTooltipWidget(int cx, int cy, int triggerRadius, List<SlotContent> allItems, ReliableClientRecipe.RecipePosition recipePosition) {
        this.cx = recipePosition.left()+ cx;
        this.cy = recipePosition.top()+ cy;
        this.triggerRadius = triggerRadius;
        this.allItems = allItems;
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(cx - triggerRadius - 9, cy - triggerRadius - 9, (triggerRadius + 9) * 2, (triggerRadius + 9) * 2);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        if (allItems.size() <= 6) return List.of();

        List<ClientTooltipComponent> tooltip = new ArrayList<>();

        tooltip.add(ClientTooltipComponent.create(Component.literal("§6Required Items:").getVisualOrderText()));

        List<ItemStack> consolidated = new ArrayList<>();
        for (SlotContent ing : allItems) {
            if (ing.getValidContents().isEmpty()) continue;

            ItemStack firstStack = ing.getValidContents().getFirst();
            boolean found = false;

            for (ItemStack existing : consolidated) {
                if (ItemStack.isSameItemSameComponents(existing, firstStack)) {
                    existing.setCount(existing.getCount() + 1);
                    found = true;
                    break;
                }
            }

            if (!found) {
				ItemStack copy = firstStack.copy();
                copy.setCount(1);
                consolidated.add(copy);
            }
        }

        for (ItemStack stack : consolidated) {
            Component name = stack.getHoverName();
            long amount = stack.count();
            Component line = Component.literal("§7- " + amount + "x ").append(name);
            tooltip.add(ClientTooltipComponent.create(line.getVisualOrderText()));
        }

        return tooltip;
    }
}