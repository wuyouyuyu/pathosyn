package com.wuyouyu.pathosynmod.item.custom;


import com.wuyouyu.pathosynmod.registry.ModComponentTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import net.minecraft.world.phys.Vec3;

import java.util.List;


public class HealingStaffItem extends Item {

    public HealingStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                int mode = stack.getOrDefault(ModComponentTypes.getHealModeComponent(), 0);
                int next = (mode + 1) % 2;
                stack.set(ModComponentTypes.getHealModeComponent(), next);
                player.displayClientMessage(Component.translatable("message.pathosyn.switched_mode", next), true);
                return InteractionResultHolder.sidedSuccess(stack, false);
            }

            int mode = stack.getOrDefault(ModComponentTypes.getHealModeComponent(), 0);

            if (isExhausted(stack)) {
                player.displayClientMessage(Component.translatable("message.pathosyn.exhausted"), true);
                return InteractionResultHolder.fail(stack);
            }

            LivingEntity target = switch (mode) {
                case 0 -> player;
                case 1 -> getLookedAtEntity(player, 5.0);
                default -> null;
            };

            if (mode == 1 && (target == null || target == player)) {
                player.displayClientMessage(Component.translatable("message.pathosyn.no_target"), true);
                level.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 1.0f, 1.0f);
                return InteractionResultHolder.fail(stack);
            }

            float healAmount = target.getMaxHealth() * 0.1f + 6f;
            target.heal(healAmount);
            stack.setDamageValue(stack.getDamageValue() + 1);

            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);
            player.displayClientMessage(Component.translatable("message.pathosyn.healed", target.getName().getString()), true);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private boolean isExhausted(ItemStack stack) {
        return stack.getDamageValue() >= 9; // Max damage is 10 → usable 0–9
    }

    private LivingEntity getLookedAtEntity(Player player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 reachVec = eyePos.add(viewVec.scale(range));
        AABB box = player.getBoundingBox().expandTowards(viewVec.scale(range)).inflate(1.0);

        List<Entity> entities = player.level().getEntities(player, box, e -> e instanceof LivingEntity && e != player && e.isPickable());
        Entity closest = null;
        double closestDist = range * range;

        for (Entity entity : entities) {
            AABB entityBox = entity.getBoundingBox().inflate(0.3);
            var optional = entityBox.clip(eyePos, reachVec);
            if (optional.isPresent()) {
                double distance = eyePos.distanceToSqr(optional.get());
                if (distance < closestDist) {
                    closest = entity;
                    closestDist = distance;
                }
            }
        }

        return (LivingEntity) closest;
    }
}
