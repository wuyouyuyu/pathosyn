package com.wuyouyu.pathosynmod.registry;

import com.wuyouyu.pathosynmod.PathosynMod;
import com.wuyouyu.pathosynmod.entity.effect.HealingBeamEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, PathosynMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<HealingBeamEntity>> HEALING_BEAM =
            ENTITIES.register("healing_beam", () ->
                    EntityType.Builder.of(HealingBeamEntity::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .build("healing_beam"));

}