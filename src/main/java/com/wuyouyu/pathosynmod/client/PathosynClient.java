package com.wuyouyu.pathosynmod.client;


import com.wuyouyu.pathosynmod.client.particle.HealingBeamParticle;
import com.wuyouyu.pathosynmod.client.particle.PathosynSymbolParticle;
import com.wuyouyu.pathosynmod.registry.ModEntities;
import com.wuyouyu.pathosynmod.registry.ModParticles;
import com.wuyouyu.pathosynmod.util.PathosynSymbol;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;

import com.wuyouyu.pathosynmod.client.renderer.NoRenderEntityRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@OnlyIn(Dist.CLIENT)
public class PathosynClient {

    // 注册粒子工厂
    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        // 注册 HealingBeam 粒子
        event.registerSpriteSet(
                ModParticles.HEALING_BEAM.get(),
                HealingBeamParticle.Provider::new
        );

        // 注册 PathosynSymbols 粒子（你的自定义精灵图粒子）
        event.registerSpriteSet(
                ModParticles.PATHOSYN_SYMBOLS.get(),
                (spriteSet) -> new PathosynSymbolParticle.Provider()
        );
    }


    // 注册实体渲染器
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HEALING_BEAM.get(), NoRenderEntityRenderer::new);

    }
}