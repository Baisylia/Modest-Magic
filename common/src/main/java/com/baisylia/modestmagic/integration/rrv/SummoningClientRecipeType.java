package com.baisylia.modestmagic.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class SummoningClientRecipeType extends AbstractModestMagicClientRecipeType {
	private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/rrv_background.png");
	public static SummoningClientRecipeType INSTANCE = new SummoningClientRecipeType();

	@Override
	public Component getDisplayName() {
		return Component.translatable("recipe.modestmagic.summoning");
	}


	@Override
	public Identifier getId() {
		return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "summoning");
	}
}
