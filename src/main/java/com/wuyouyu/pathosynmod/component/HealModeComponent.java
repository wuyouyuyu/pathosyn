package com.wuyouyu.pathosynmod.component;


import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class HealModeComponent {
    public static final ResourceKey<DataComponentType<?>> HEAL_MODE =
            ResourceKey.create(
                    Registries.DATA_COMPONENT_TYPE,
                    ResourceLocation.fromNamespaceAndPath("pathosyn", "heal_mode")
            );
}