package com.baisylia.modestmagic;

import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.block.entity.ModBlockEntities;
import com.baisylia.modestmagic.client.ModSounds;
import com.baisylia.modestmagic.config.ModConfig;
import com.baisylia.modestmagic.item.ModItems;
import com.baisylia.modestmagic.recipe.ModRecipes;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;

public class ModestMagic {
    public ModestMagic() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModestMagicConfig.SPEC);

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::commonSetup);

        ModConfig.load();
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.register(eventBus);
        ModRecipes.register(eventBus);
        ModSounds.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.PEDESTAL.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALTAR.get(), RenderType.cutoutMipped());
        }

        @SubscribeEvent
        public static void onTextureStitch(TextureStitchEvent.Pre event) {
            if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
                event.addSprite(new ResourceLocation(ModestMagic.MOD_ID, "item/empty_slot_tablet"));
            }
        }
    }
}