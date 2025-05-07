package com.wuyouyu.pathosynmod.event;

import com.wuyouyu.pathosynmod.PathosynMod;
import com.wuyouyu.pathosynmod.item.custom.HealingStaffItem;

import com.wuyouyu.pathosynmod.registry.ModComponentTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;





@EventBusSubscriber(modid = PathosynMod.MODID)
public class HealingStaffEvents {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof HealingStaffItem) {
            int current = stack.getOrDefault(ModComponentTypes.HEAL_MODE.value(), 0);
            int next = (current + 1) % 2;

            stack.set(ModComponentTypes.HEAL_MODE.value(), next);
            player.displayClientMessage(Component.literal("Mode switched to " + next), true);
        }
    }
}