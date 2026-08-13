package com.baisylia.modestmagic.integration.rrv;

import com.baisylia.modestmagic.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class InfusingClientRecipeType extends AbstractModestMagicClientRecipeType {
	public static InfusingClientRecipeType INSTANCE = new InfusingClientRecipeType();

	@Override
	public Component getDisplayName() {
		return Component.translatable("recipe.modestmagic.infusing");
	}

	@Override
	public Identifier getId() {
		return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "infusing");
	}
}
