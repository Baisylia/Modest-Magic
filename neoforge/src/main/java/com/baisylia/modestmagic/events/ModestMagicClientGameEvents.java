package com.baisylia.modestmagic.events;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.client.TabletTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ModestMagicClientGameEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        TabletTooltip.appendHoverText(event.getItemStack(), event.getContext(), event.getToolTip(), event.getFlags());
    }
}
