package com.baisylia.modestmagic.block.renderer;

import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;
import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class AltarRenderer implements BlockEntityRenderer<AltarBlockEntity, PedestalBlockEntityRenderState> {

    private final ItemModelResolver itemModelResolver;

    public AltarRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();

    }

    @Override
    public void extractRenderState(AltarBlockEntity enchantingTable, PedestalBlockEntityRenderState state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(enchantingTable, state, partialTick, cameraPosition, breakProgress);
        ItemStackRenderState itemState = new ItemStackRenderState();
        this.itemModelResolver.updateForTopItem(itemState, enchantingTable.getItem(), ItemDisplayContext.FIXED, enchantingTable.getLevel(), null, 0);
        state.item = itemState;
        state.rotation = ((enchantingTable.getLevel().getGameTime() % 360) + partialTick) * 2f;
    }

    @Override
    public PedestalBlockEntityRenderState createRenderState() {
        return new PedestalBlockEntityRenderState();
    }

    @Override
    public void submit(PedestalBlockEntityRenderState enchantingTable, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        ItemStackRenderState stack = enchantingTable.item;
        if (stack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.4D, 0.5D);

        float rotation = enchantingTable.rotation;

        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.scale(0.6f, 0.6f, 0.6f);

        stack.submit(poseStack, submitNodeCollector, enchantingTable.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}