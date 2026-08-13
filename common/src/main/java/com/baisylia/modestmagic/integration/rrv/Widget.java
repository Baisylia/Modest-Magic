package com.baisylia.modestmagic.integration.rrv;

import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public abstract class Widget implements Renderable, GuiEventListener, NarratableEntry {

	public abstract Bounds getBounds();

	public abstract void extractRenderState(GuiGraphicsExtractor draw, int mouseX, int mouseY, float delta);

	public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
		return List.of();
	}

	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		return false;
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	@Override
	public void setFocused(boolean b) {

	}

	@Override
	public boolean isFocused() {
		return false;
	}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {

	}
}