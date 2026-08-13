package com.baisylia.modestmagic.events;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.block.renderer.AltarRenderer;
import com.baisylia.modestmagic.block.renderer.PedestalRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderGetter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ModestMagicClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PEDESTAL_BLOCK_ENTITY.get(), PedestalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), AltarRenderer::new);
    }

	public static HolderGetter.Provider getRegistryAccess() {
		return Minecraft.getInstance().level.registryAccess();
	}
}