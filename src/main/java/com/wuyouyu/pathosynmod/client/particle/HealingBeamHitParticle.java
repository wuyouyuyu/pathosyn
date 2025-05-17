package com.wuyouyu.pathosynmod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class HealingBeamHitParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final float baseSize;

    protected HealingBeamHitParticle(ClientLevel level, double x, double y, double z,
                                     double xd, double yd, double zd, SpriteSet spriteSet) {
        super(level, x, y, z, xd, yd, zd);
        this.spriteSet = spriteSet;
        this.gravity = 0.0F;
        this.lifetime = 20; // 寿命延长为20 ticks（1秒）
        // 尺寸差异
        this.baseSize = 0.1f + level.random.nextFloat() * 0.9f;  // 范围
        this.quadSize = baseSize;
        this.setSpriteFromAge(spriteSet);
        this.xd = 0.0;
        this.yd = 0.08;
        this.zd = 0.0;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.alpha = 1.0F;
        this.hasPhysics = true;
        this.friction = 0.7f;  // 有阻力，越小越慢

    }

    @Override
    public void tick() {
        super.tick();
        // 保持不变
        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new HealingBeamHitParticle(level, x, y, z, xd, yd, zd, this.sprite);
        }
    }
}


