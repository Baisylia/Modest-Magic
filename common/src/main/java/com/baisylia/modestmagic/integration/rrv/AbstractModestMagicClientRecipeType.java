package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class AbstractModestMagicClientRecipeType implements ReliableClientRecipeType {
	private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/rrv_background.png");

	@Override
	public int getDisplayWidth() {
		return 140;
	}

	@Override
	public int getDisplayHeight() {
		return 80;
	}

	@Override
	public @Nullable Identifier getGuiTexture() {
		return BACKGROUND;
	}

	@Override
	public int getSlotCount() {
		return 1;
	}

	@Override
	public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
		slotDefinition.addItemSlot(0, getDisplayWidth() - 18, getDisplayHeight() - 18);
	}

	@Override
	public List<ItemStack> getCraftReferences() {
		return List.of(new ItemStack(ModBlocks.ALTAR.get()));
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(ModBlocks.ALTAR.get());
	}

	@Override
	public ButtonData placeRecipeShareButton(RecipeViewMenu.DisplayInfo info) {
		return new ButtonData(info.guiLeft() + getDisplayWidth() + 2, info.guiTop() + getDisplayHeight() / 2 - 6, false);
	}
}
