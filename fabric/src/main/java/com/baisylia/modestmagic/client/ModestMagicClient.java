package com.baisylia.modestmagic.client;

import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.block.renderer.AltarRenderer;
import com.baisylia.modestmagic.block.renderer.PedestalRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class ModestMagicClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY.get(), PedestalRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), AltarRenderer::new);

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) ->
                TabletTooltip.appendHoverText(stack, context, lines, type));
    }
}