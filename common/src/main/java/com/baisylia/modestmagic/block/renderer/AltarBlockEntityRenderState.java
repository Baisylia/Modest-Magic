package com.baisylia.modestmagic.block.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class AltarBlockEntityRenderState extends BlockEntityRenderState {
	public ItemStackRenderState item;
	public @Nullable float rotation;
}
