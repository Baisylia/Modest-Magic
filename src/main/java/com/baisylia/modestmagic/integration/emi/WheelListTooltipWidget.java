package com.baisylia.modestmagic.integration.emi;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class WheelListTooltipWidget extends Widget {
    private final int cx, cy, triggerRadius;
    private final List<EmiIngredient> allItems;

    public WheelListTooltipWidget(int cx, int cy, int triggerRadius, List<EmiIngredient> allItems) {
        this.cx = cx;
        this.cy = cy;
        this.triggerRadius = triggerRadius;
        this.allItems = allItems;
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(cx - triggerRadius - 9, cy - triggerRadius - 9, (triggerRadius + 9) * 2, (triggerRadius + 9) * 2);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        if (allItems.size() <= 6) return List.of();

        List<ClientTooltipComponent> tooltip = new ArrayList<>();

        tooltip.add(ClientTooltipComponent.create(Component.literal("§6Required Items:").getVisualOrderText()));

        List<EmiStack> consolidated = new ArrayList<>();
        for (EmiIngredient ing : allItems) {
            if (ing.getEmiStacks().isEmpty()) continue;

            EmiStack firstStack = ing.getEmiStacks().get(0);
            boolean found = false;

            for (EmiStack existing : consolidated) {
                if (existing.isEqual(firstStack)) {
                    existing.setAmount(existing.getAmount() + 1);
                    found = true;
                    break;
                }
            }

            if (!found) {
                EmiStack copy = firstStack.copy();
                copy.setAmount(1);
                consolidated.add(copy);
            }
        }

        for (EmiStack stack : consolidated) {
            Component name = stack.getItemStack().getHoverName();
            long amount = stack.getAmount();
            Component line = Component.literal("§7- " + amount + "x ").append(name);
            tooltip.add(ClientTooltipComponent.create(line.getVisualOrderText()));
        }

        return tooltip;
    }
}