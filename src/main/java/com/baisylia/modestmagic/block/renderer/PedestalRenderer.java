package com.baisylia.modestmagic.block.renderer;

import com.baisylia.modestmagic.block.entity.custom.PedestalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Vector3f;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {

    public PedestalRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(PedestalBlockEntity pedestal, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        ItemStack stack = pedestal.getItem();
        if(stack.isEmpty()) return;

        poseStack.pushPose();

        poseStack.translate(0.5, 1.25, 0.5);

        float rotation = (pedestal.getLevel().getGameTime() + partialTick) * 2;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        poseStack.scale(1.3f,1.3f,1.3f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemTransforms.TransformType.GROUND,
                light,
                overlay,
                poseStack,
                buffer,
                0
        );

        poseStack.popPose();
    }
}