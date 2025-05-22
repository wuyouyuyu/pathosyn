package com.wuyouyu.pathosynmod.component;

import com.wuyouyu.pathosynmod.registry.ModComponentTypes;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ChargeCountComponent {

    public static final int MAX_CHARGES = 10;

    public static final ResourceKey<DataComponentType<?>> KEY = ResourceKey.create(
            Registries.DATA_COMPONENT_TYPE,
            ResourceLocation.fromNamespaceAndPath("pathosyn", "charge_count")
    );

    // 获取当前充能值，如果未初始化，默认返回 0
    public static int get(ItemStack stack) {
        return stack.getOrDefault(ModComponentTypes.getChargeCountComponent(), 0);
    }


    public static void set(ItemStack stack, int value) {
        stack.set(ModComponentTypes.getChargeCountComponent(), value);
    }


    public static void consume(ItemStack stack) {
        set(stack, Math.max(0, get(stack) - 1));
    }

    // 是否已耗尽
    public static boolean isExhausted(ItemStack stack) {
        return get(stack) <= 0;
    }

    //重置为最大值
    public static void reset(ItemStack stack) {
        set(stack, MAX_CHARGES);
    }
}