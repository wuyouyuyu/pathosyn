package com.wuyouyu.pathosynmod.component;


import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public class StaffModeComponent {
    public static final ResourceKey<DataComponentType<?>> MODE =
            ResourceKey.create(
                    Registries.DATA_COMPONENT_TYPE,
                    ResourceLocation.fromNamespaceAndPath("pathosyn", "staff_mode")
            );

    public static final DataComponentType<Integer> MODE_TYPE =
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build();

    public static void register(RegisterEvent event) {
        event.register(Registries.DATA_COMPONENT_TYPE, helper -> {
            helper.register(MODE.location(), MODE_TYPE);
        });
    }

    public static DataComponentType<Integer> getModeComponent() {
        return MODE_TYPE;
    }
}