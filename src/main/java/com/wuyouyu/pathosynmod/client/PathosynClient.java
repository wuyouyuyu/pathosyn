package com.wuyouyu.pathosynmod.client;

import com.wuyouyu.pathosynmod.client.particle.HealingBeamParticle;
import com.wuyouyu.pathosynmod.registry.ModEntities;
import com.wuyouyu.pathosynmod.registry.ModParticles;
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

    // 注册粒子
    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.HEALING_BEAM.get(), HealingBeamParticle.Provider::new);
    }

    // 注册实体渲染器
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HEALING_BEAM.get(), NoRenderEntityRenderer::new);
    }
}