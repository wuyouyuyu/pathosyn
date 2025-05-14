package com.wuyouyu.pathosynmod.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HealingBeamParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    protected HealingBeamParticle(ClientLevel level, double x, double y, double z,
                                  double xd, double yd, double zd,
                                  SpriteSet spriteSet) {
        super(level, x, y, z, xd, yd, zd);
        this.spriteSet = spriteSet;

        // 基础视觉参数
        this.gravity = 0.0F;                          // 不受重力影响
        this.lifetime = 10;                           // 粒子寿命（tick）
        this.quadSize = 1.0F;                         // 大小 1x1
        this.setSpriteFromAge(spriteSet);            // 使用 sprite 动画
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.alpha = 1.0F;

        // 初始速度
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);
       // 按年龄更新贴图帧
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
            return new HealingBeamParticle(level, x, y, z, xd * 11, yd * 11, zd * 11, this.sprite);
        }
    }
}
