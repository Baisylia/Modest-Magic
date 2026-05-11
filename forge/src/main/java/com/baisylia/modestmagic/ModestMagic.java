package com.baisylia.modestmagic;

import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.client.ClientConfigSetup;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Map;
import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class ModestMagic {
    public ModestMagic() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register(ModLoadingContext.get().getActiveContainer());
        }
        MinecraftForge.EVENT_BUS.addListener(this::onFuelBurnTime);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CommonClass::init);
    }

    private void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        for (Map.Entry<Supplier<Item>, Integer> entry : ModBlocks.FUEL_ITEMS.entrySet()) {
            if (event.getItemStack().is(entry.getKey().get())) {
                event.setBurnTime(entry.getValue());
                return;
            }
        }
    }
}