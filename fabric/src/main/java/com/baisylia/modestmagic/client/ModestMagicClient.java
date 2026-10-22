package com.baisylia.modestmagic.client;

import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.block.renderer.AltarRenderer;
import com.baisylia.modestmagic.block.renderer.PedestalRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.HolderGetter;

public class ModestMagicClient implements ClientModInitializer {

    public static HolderGetter.Provider getRegistryAccess() {
        return Minecraft.getInstance().level.registryAccess();
    }

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY.get(), PedestalRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), AltarRenderer::new);

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) ->
                TabletTooltip.appendHoverText(stack, context, lines, type));
    }
}