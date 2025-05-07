package com.wuyouyu.pathosynmod.item.custom;


import com.wuyouyu.pathosynmod.registry.ModComponentTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class HealingStaffItem extends Item {
    public HealingStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            int currentDamage = stack.getDamageValue();
            int maxDamage = stack.getMaxDamage();

            if (currentDamage >= maxDamage) {
                player.displayClientMessage(Component.literal("法杖已耗尽能量，无法再使用"), true);
                return InteractionResultHolder.fail(stack);
            }

            int mode = stack.getOrDefault(ModComponentTypes.HEAL_MODE.value(), 0);
            LivingEntity target = switch (mode) {
                case 0 -> player;
                case 1 -> getLookedAtEntity(player, 5.0);
                default -> null;
            };

            if (target == null) {
                player.displayClientMessage(Component.literal("没有目标可以治疗"), true);
                level.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.8f, 1.0f);
                return InteractionResultHolder.fail(stack);
            }

            System.out.println("[治疗触发] 目标: " + target.getName().getString() + "（UUID: " + target.getUUID() + "）");

            float healAmount = target.getMaxHealth() * 0.1f + 6f;
            target.heal(healAmount);
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);
            stack.setDamageValue(currentDamage + 1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private LivingEntity getLookedAtEntity(Player player, double range) {
        HitResult hitResult = player.pick(range, 0.0f, false);
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (entityHit.getEntity() instanceof LivingEntity target && target != player) {
                return target;
            }
        }
        return null;
    }
}