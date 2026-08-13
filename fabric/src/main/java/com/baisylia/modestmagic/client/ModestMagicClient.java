package com.baisylia.modestmagic.client;

import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.block.renderer.AltarRenderer;
import com.baisylia.modestmagic.block.renderer.PedestalRenderer;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderGetter;

public class ModestMagicClient implements ClientModInitializer {

	public static HolderGetter.Provider getRegistryAccess() {
		return Minecraft.getInstance().level.registryAccess();
	}

	@Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY.get(), PedestalRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), AltarRenderer::new);
    }
}