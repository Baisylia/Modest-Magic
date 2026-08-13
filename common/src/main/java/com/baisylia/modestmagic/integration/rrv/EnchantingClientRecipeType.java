package com.baisylia.modestmagic.integration.rrv;

import com.baisylia.modestmagic.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class EnchantingClientRecipeType extends AbstractModestMagicClientRecipeType {
	private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/rrv_background.png");
	public static EnchantingClientRecipeType INSTANCE = new EnchantingClientRecipeType();

	@Override
	public Component getDisplayName() {
		return Component.translatable("recipe.modestmagic.enchanting");
	}

	@Override
	public @Nullable Identifier getGuiTexture() {
		return BACKGROUND;
	}

	@Override
	public Identifier getId() {
		return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "enchanting");
	}

}
