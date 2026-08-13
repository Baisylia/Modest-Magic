package com.baisylia.modestmagic.block.renderer;

import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity, PedestalBlockEntityRenderState> {

    private final ItemModelResolver itemModelResolver;

    public PedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public PedestalBlockEntityRenderState createRenderState() {
        return new PedestalBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(PedestalBlockEntity pedestal, PedestalBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(pedestal, state, partialTicks, cameraPosition, breakProgress);
        ItemStackRenderState itemState = new ItemStackRenderState();
        this.itemModelResolver.updateForTopItem(itemState, pedestal.getItem(), ItemDisplayContext.FIXED, pedestal.getLevel(), null, 0);
        state.item = itemState;
        state.rotation = ((pedestal.getLevel().getGameTime() % 360) + partialTicks) * 2f;
    }

    @Override
    public void submit(PedestalBlockEntityRenderState pedestal, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        ItemStackRenderState stack = pedestal.item;
        if (stack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.35D, 0.5D);

        float rotation = pedestal.rotation;

        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.scale(0.6f, 0.6f, 0.6f);

        stack.submit(poseStack, submitNodeCollector, pedestal.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}