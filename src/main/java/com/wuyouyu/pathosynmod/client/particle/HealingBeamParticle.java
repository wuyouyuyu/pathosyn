package com.wuyouyu.pathosynmod.client.particle;

import net.minecraft.world.level.Level;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class HealingBeamParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final float baseSize;

    protected HealingBeamParticle(ClientLevel level, double x, double y, double z,
                                  double xd, double yd, double zd, SpriteSet spriteSet) {
        super(level, x, y, z, xd, yd, zd);
        this.spriteSet = spriteSet;
        this.gravity = 0.0F;
        this.lifetime = 10;
        this.baseSize = 0.3f;
        this.quadSize = baseSize;
        this.setSpriteFromAge(spriteSet);
        this.rCol = 0.4F;
        this.gCol = 0.6F;
        this.bCol = 1.0F;
        this.alpha = 1.0F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
    }

    @Override
    public void tick() {
        super.tick();

        float progress = (float) this.age / this.lifetime;
        float inverse = 1.0f - progress;

        // 粒子大小随时间缩小
        this.quadSize = baseSize * inverse;

        // 粒子透明度随时间减弱
        this.alpha = inverse;

        // 坐标每刻偏移
        double spiralRadius = 0.05 * inverse;
        this.x += 0;
        this.z += 0;

        // 粒子颜色渐变
        if (progress < 0.03f) {
            this.rCol = 0.4f;
            this.gCol = 0.6f;
            this.bCol = 1.0f;
        } else if (progress < 0.73f) {
            this.rCol = 0.5f;
            this.gCol = 0.5f;
            this.bCol = 1.0f;
        } else {
            this.rCol = 0.8f;
            this.gCol = 0.6f;
            this.bCol = 1.0f;
        }

        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new HealingBeamParticle(level, x, y, z, xd , yd , zd , this.sprite);
        }
    }
}
