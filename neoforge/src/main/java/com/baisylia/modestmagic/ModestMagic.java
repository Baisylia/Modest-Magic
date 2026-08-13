package com.baisylia.modestmagic;

import com.baisylia.modestmagic.block.ModBlocks;
import com.baisylia.modestmagic.client.ClientConfigSetup;
import com.baisylia.modestmagic.item.ModItems;
import com.baisylia.modestmagic.recipe.ModRecipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;
import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
@EventBusSubscriber(modid = Constants.MOD_ID)
public class ModestMagic {

    public static MinecraftServer SERVER = null;
    public static RecipeMap MAP;

    public ModestMagic(IEventBus modEventBus, ModContainer modContainer) {
//        NeoForgeRegistryHelper.register(modEventBus);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ClientConfigSetup.register(modContainer);
        }
    }

    @SubscribeEvent
    private static void register(ServerStartedEvent event) {
        SERVER = event.getServer();
    }

    @SubscribeEvent
    private static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            CommonClass.init();
        }
    }

    @SubscribeEvent
    private static void sendRecipes(OnDatapackSyncEvent event) {
        event.sendRecipes(ModRecipes.ENCHANTING_TYPE.get(), ModRecipes.INFUSING_TYPE.get(), ModRecipes.SUMMONING_TYPE.get(), RecipeType.SMITHING);
    }

    @SubscribeEvent
    private static void receiveRecipes(RecipesReceivedEvent event) {
        MAP = event.getRecipeMap();
    }

    @SubscribeEvent
    private static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        for (Map.Entry<Supplier<Item>, Integer> entry : ModBlocks.FUEL_ITEMS.entrySet()) {
            if (event.getItemStack().is(entry.getKey().get())) {
                event.setBurnTime(entry.getValue());
                return;
            }
        }
    }

    @SubscribeEvent
    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
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