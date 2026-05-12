package com.baisylia.modestmagic;

import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.client.ClientConfigSetup;
import com.baisylia.modestmagic.item.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Map;
import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class ModestMagic {
    public ModestMagic() {
        CommonClass.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::addCreative);

        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register(ModLoadingContext.get().getActiveContainer());
        }

        MinecraftForge.EVENT_BUS.addListener(this::onFuelBurnTime);
    }

    private void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        for (Map.Entry<Supplier<Item>, Integer> entry : ModBlocks.FUEL_ITEMS.entrySet()) {
            if (event.getItemStack().is(entry.getKey().get())) {
                event.setBurnTime(entry.getValue());
                return;
            }
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.STARDUST.get());
            event.accept(ModItems.ENCHANTMENT_TABLET.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_AQUATIC.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_CYCLIC.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_EVERLASTING.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_FLINGING.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_GLACIAL.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_HALLOWED.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_HAUNTED.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_HEAVY.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_INFESTED.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_RECOLLECTION.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_SWIFT.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_FIERY.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_LIGHT.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_LUCKY.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_SERRATED.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_PUNCTURING.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_ENDURING.get());
            event.accept(ModItems.ENCHANTMENT_TABLET_SECURE.get());
        } else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.ALTAR.get());
            event.accept(ModBlocks.PEDESTAL.get());
        }
    }
}