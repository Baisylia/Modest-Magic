package com.baisylia.modestmagic.mixin.client;

import com.baisylia.modestmagic.Constants;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(SmithingScreen.class)
public class SmithingScreenMixin {

    @Unique
    private static final ResourceLocation TABLET_EMPTY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/empty_slot_tablet");

    @ModifyArg(
            method = "containerTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CyclingSlotBackground;tick(Ljava/util/List;)V", ordinal = 0),
            index = 0
    )
    private List<ResourceLocation> modestmagic$addTabletToTemplateSlotIcons(List<ResourceLocation> originalIcons) {
        List<ResourceLocation> combinedIcons = new ArrayList<>(originalIcons);
        combinedIcons.add(TABLET_EMPTY_SLOT);

        return combinedIcons;
    }
}