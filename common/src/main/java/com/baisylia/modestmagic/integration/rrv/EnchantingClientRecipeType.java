package com.baisylia.modestmagic.integration.rrv;

import com.baisylia.modestmagic.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class EnchantingClientRecipeType extends AbstractModestMagicClientRecipeType {
	public static EnchantingClientRecipeType INSTANCE = new EnchantingClientRecipeType();

	@Override
	public Component getDisplayName() {
		return Component.translatable("recipe.modestmagic.enchanting");
	}

	@Override
	public Identifier getId() {
		return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "enchanting");
	}

}
