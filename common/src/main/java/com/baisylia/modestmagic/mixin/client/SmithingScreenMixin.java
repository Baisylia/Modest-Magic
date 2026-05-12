package com.baisylia.modestmagic.mixin.client;

import com.baisylia.modestmagic.Constants;
import com.baisylia.modestmagic.mixin.accessor.AbstractContainerScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreen.class)
public class SmithingScreenMixin {

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void modestmagic$renderTabletSlot(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        SmithingScreen screen = (SmithingScreen) (Object) this;

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        Slot additionSlot = screen.getMenu().getSlot(2);

        int x = accessor.getLeftPos() + additionSlot.x;
        int y = accessor.getTopPos() + additionSlot.y;

        boolean hasItem = additionSlot.hasItem();
        boolean showTablet = (System.currentTimeMillis() / 1500L) % 2 != 0;

        if (hasItem || showTablet) {
            guiGraphics.fill(x, y, x + 16, y + 16, 0xFF8B8B8B);

            if (!hasItem) {
                TextureAtlasSprite tabletSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(new ResourceLocation(Constants.MOD_ID, "item/empty_slot_tablet"));

                guiGraphics.blit(x, y, 0, 16, 16, tabletSprite);
            }
        }
    }
}