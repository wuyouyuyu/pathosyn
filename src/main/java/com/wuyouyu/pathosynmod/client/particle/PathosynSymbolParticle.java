package com.wuyouyu.pathosynmod.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wuyouyu.pathosynmod.util.PathosynSymbol;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class PathosynSymbolParticle extends TextureSheetParticle {
    private final float baseSize;
    private final float u0, v0, u1, v1;

    // ——构造函数：只传递参数，不处理工厂/注册逻辑——
    protected PathosynSymbolParticle(ClientLevel level, double x, double y, double z,
                                     PathosynSymbol symbol, float baseSize) {
        super(level, x, y, z, 0, 0, 0);

        PathosynSymbol.UVRect uv = symbol.getUV();
        this.u0 = uv.u0;
        this.v0 = uv.v0;
        this.u1 = uv.u1;
        this.v1 = uv.v1;

        this.baseSize = baseSize;
        this.quadSize = baseSize;

        this.lifetime = 20;
        this.gravity = 0.0F;
        this.xd = 0.0;
        this.yd = 0.08;
        this.zd = 0.0;
        this.hasPhysics = true;
        this.friction = 0.7f;

        this.rCol = symbol.r;
        this.gCol = symbol.g;
        this.bCol = symbol.b;
        this.alpha = symbol.a;
    }

    // ——tick可根据需要复写动画、变色、缩放逻辑——
    @Override
    public void tick() {
        super.tick();
        // 可扩展：粒子渐隐/变大等效果

    }

    // ——交给MC自动渲染，直接返回UV即可——
    @Override public float getU0() { return u0; }
    @Override public float getU1() { return u1; }
    @Override public float getV0() { return v0; }
    @Override public float getV1() { return v1; }
    @Override public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    // ——工厂Provider，统一管理参数，便于注册和扩展——
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            // 用dx/dy/dz作为参数，比如dx=帧号，dy/dz=颜色（0~1）
            int frame = (int) dx;
            float color = (float) dy;
            PathosynSymbol symbol = new PathosynSymbol(frame, color, 1f, 1f, 1f); // 假设只有R通道变
            return new PathosynSymbolParticle(level, x, y, z, symbol, 0.5f);
        }
    }
}