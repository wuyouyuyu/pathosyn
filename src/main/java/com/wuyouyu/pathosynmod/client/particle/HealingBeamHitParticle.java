package com.wuyouyu.pathosynmod.client.particle;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;


@OnlyIn(Dist.CLIENT)
public class HealingBeamHitParticle extends SingleQuadParticle {
    private final TextureAtlasSprite sprite;
    private final ParticleFrameData.FrameRect rect;
    private final float baseSize;
    // ...其它参数

    public HealingBeamHitParticle(
            ClientLevel level, double x, double y, double z,
            float r, float g, float b, float size,
            TextureAtlasSprite sprite,
            ParticleFrameData.FrameRect rect
    ) {
        super(level, x, y, z);
        this.baseSize = size;
        this.quadSize = size;
        this.sprite = sprite;
        this.rect = rect;
        this.rCol = r; this.gCol = g; this.bCol = b;
        // ...其它初始化
    }

    private float[] calcUV() {
        float u0 = sprite.getU(rect.x());
        float u1 = sprite.getU(rect.x() + rect.width());
        float v0 = sprite.getV(rect.y());
        float v1 = sprite.getV(rect.y() + rect.height());
        return new float[]{u0, u1, v0, v1};
    }

    @Override protected float getU0() { return calcUV()[0]; }
    @Override protected float getU1() { return calcUV()[1]; }
    @Override protected float getV0() { return calcUV()[2]; }
    @Override protected float getV1() { return calcUV()[3]; }

    @Override
    public int getLightColor(float partialTick) { return 0xF000F0; }

    @Override
    public void tick() {
        super.tick();
        float progress = (float) this.age / this.lifetime;
        this.alpha = 1.0f - progress;
        this.quadSize = baseSize * (0.85f + 0.25f * (1 - progress));
        // 粒子颜色渐变，在这里更新 this.rCol, this.gCol, this.bCol
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;

            // 尝试打印所有实际 SpriteSet 里的 sprite 名字（只做 debug）
            try {
                Field spritesField = spriteSet.getClass().getDeclaredField("sprites");
                spritesField.setAccessible(true);
                @SuppressWarnings("unchecked")
                java.util.List<TextureAtlasSprite> sprites = (java.util.List<TextureAtlasSprite>) spritesField.get(spriteSet);
                for (int i = 0; i < sprites.size(); i++) {
                    System.out.println("SpriteSet[" + i + "]: " + sprites.get(i).contents().name());
                }
            } catch (Exception e) {
                System.out.println("无法打印 SpriteSet 贴图: " + e);
            }
        }
        @Override
        public Particle createParticle(
                @NotNull SimpleParticleType type, @NotNull ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd
        ) {
            float r = (float) xd;
            float g = (float) yd;
            float b = (float) zd;
            int frameIndex = (int) z;

            // 只用一张大贴图（SpriteSet），实际帧区域用FrameRect[]描述
            TextureAtlasSprite sprite = spriteSet.get(level.random); // 拿整张精灵图
            // 安全校验
            ParticleFrameData.FrameRect rect;
            if (frameIndex >= 0 && frameIndex < ParticleFrameData.HEALING_FRAMES.length) {
                rect = ParticleFrameData.HEALING_FRAMES[frameIndex];
            } else {
                rect = ParticleFrameData.HEALING_FRAMES[0]; // 或抛异常
            }

            return new HealingBeamHitParticle(
                    level, x, y, z, r, g, b, 0.36f, // 可参数化尺寸
                    sprite, rect
            );
        }
    }
}

