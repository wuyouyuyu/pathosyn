package com.wuyouyu.pathosynmod.registry;


import com.wuyouyu.pathosynmod.PathosynMod;
import com.wuyouyu.pathosynmod.component.HealModeComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;
import com.mojang.serialization.Codec;


public class ModComponentTypes {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModComponentTypes::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        event.register(Registries.DATA_COMPONENT_TYPE, helper -> {
            DataComponentType<Integer> type = DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build();

            helper.register(
                    HealModeComponent.HEAL_MODE.location(),
                    type
            );
        });
    }
    @SuppressWarnings("unchecked")
    public static DataComponentType<Integer> getHealModeComponent() {
        return (DataComponentType<Integer>)
                BuiltInRegistries.DATA_COMPONENT_TYPE.get(HealModeComponent.HEAL_MODE.location());
    }
}