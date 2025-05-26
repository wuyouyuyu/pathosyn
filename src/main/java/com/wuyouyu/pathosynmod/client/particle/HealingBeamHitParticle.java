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
        System.out.println("【全图测试】rect=(0,0,128,128), u0=" + u0 + ", u1=" + u1 + ", v0=" + v0 + ", v1=" + v1);
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
        private static int printCount = 0;

        private static boolean printed = false;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;

        }
        @Override
        public Particle createParticle(
                SimpleParticleType type, ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd
        ) {
            int frameIndex = (int) xd;
            TextureAtlasSprite sprite = spriteSet.get(level.random);

            // 打印前16帧的帧号和切割坐标
            if (printCount < 16) {
                ParticleFrameData.FrameRect rect = ParticleFrameData.HEALING_FRAMES[
                        frameIndex >= 0 && frameIndex < ParticleFrameData.HEALING_FRAMES.length ? frameIndex : 0
                        ];
                System.out.println(
                        "帧 index=" + frameIndex +
                                ", rect: x=" + rect.x() +
                                ", y=" + rect.y() +
                                ", width=" + rect.width() +
                                ", height=" + rect.height()
                );
                System.out.println("sprite min U,V: " + sprite.getU0() + "," + sprite.getV0());
                System.out.println("sprite max U,V: " + sprite.getU1() + "," + sprite.getV1());
                System.out.println("sprite width=" + sprite.contents().width() + ", height=" + sprite.contents().height());
                System.out.println("rect: x=" + rect.x() + ", y=" + rect.y() + ", w=" + rect.width() + ", h=" + rect.height());
                System.out.println(
                        "贴图宽度=" + sprite.contents().width() +
                                ", 贴图高度=" + sprite.contents().height()
                );
                printCount++;
            }

            ParticleFrameData.FrameRect rect = ParticleFrameData.HEALING_FRAMES[
                    frameIndex >= 0 && frameIndex < ParticleFrameData.HEALING_FRAMES.length ? frameIndex : 0
                    ];
            return new HealingBeamHitParticle(level, x, y, z, 0.2f, 0.95f, 0.35f, 0.36f, sprite, rect);
        }
    }
}

