package com.wuyouyu.pathosynmod.client;

import com.wuyouyu.pathosynmod.registry.ModEntities;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import com.wuyouyu.pathosynmod.client.renderer.NoRenderEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class PathosynClient {
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.HEALING_BEAM.get(),
                NoopRenderer::new);
    }
}