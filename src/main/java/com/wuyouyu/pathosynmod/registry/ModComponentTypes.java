package com.wuyouyu.pathosynmod.registry;


import com.mojang.serialization.Codec;
import com.wuyouyu.pathosynmod.component.StaffModeComponent;
import com.wuyouyu.pathosynmod.item.custom.ChargeCountComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;






public class ModComponentTypes {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModComponentTypes::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        StaffModeComponent.register(event);
        event.register(Registries.DATA_COMPONENT_TYPE, helper -> {
            DataComponentType<Integer> chargeType = DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build();
            helper.register(ChargeCountComponent.KEY.location(), chargeType);

        });
    }

    @SuppressWarnings("unchecked")
    public static DataComponentType<Integer> getChargeCountComponent() {
        return (DataComponentType<Integer>) BuiltInRegistries.DATA_COMPONENT_TYPE
                .get(ChargeCountComponent.KEY.location());
    }
}