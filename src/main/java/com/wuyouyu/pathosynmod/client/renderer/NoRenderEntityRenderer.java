package com.wuyouyu.pathosynmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class NoRenderEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    public NoRenderEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null; // 不需要纹理
    }

    @Override
    public void render(@NotNull T entity, float yaw, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer, int packedLight) {
        // 不渲染任何东西
    }
}