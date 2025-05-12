package com.wuyouyu.pathosynmod.client.renderer;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NoRenderEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    public NoRenderEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation( T entity) {
        return null;
    }

    @Override
    public boolean shouldRender(@NotNull T entity, @NotNull Frustum camera, double camX, double camY, double camZ) {
        return false; // 不渲染实体模型
    }
}