package com.baisylia.modestmagic.block.renderer;

import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class AltarRenderer implements BlockEntityRenderer<AltarBlockEntity> {

    public AltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AltarBlockEntity enchantingTable, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        ItemStack stack = enchantingTable.getItem();
        if (stack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.4D, 0.5D);

        float rotation = ((enchantingTable.getLevel().getGameTime() % 360) + partialTick) * 2f;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        poseStack.scale(0.6f, 0.6f, 0.6f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemTransforms.TransformType.FIXED,
                light,
                overlay,
                poseStack,
                buffer,
                0
        );

        poseStack.popPose();
    }
}