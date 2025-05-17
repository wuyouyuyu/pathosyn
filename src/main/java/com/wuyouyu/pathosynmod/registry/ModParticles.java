package com.wuyouyu.pathosynmod.registry;

import com.wuyouyu.pathosynmod.PathosynMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, PathosynMod.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HEALING_BEAM =
            PARTICLES.register("healing_beam", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HEALING_BEAM_HIT =
            PARTICLES.register("healing_beam_hit", () -> new SimpleParticleType(true));
}
