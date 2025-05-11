package com.wuyouyu.pathosynmod.item.custom;


import com.wuyouyu.pathosynmod.component.StaffModeComponent;

import com.wuyouyu.pathosynmod.registry.ModComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HealingStaffItem extends Item {

    public static final int MAX_CHARGES = 10;

    public HealingStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                int mode = stack.getOrDefault(StaffModeComponent.getModeComponent(), 0);
                int next = (mode + 1) % 2;
                stack.set(StaffModeComponent.getModeComponent(), next);

                return InteractionResultHolder.sidedSuccess(stack, false);
            }

            int mode = stack.getOrDefault(StaffModeComponent.getModeComponent(), 0);

            if (isExhausted(stack)) {
                player.displayClientMessage(Component.translatable("message.pathosyn.exhausted"), true);
                return InteractionResultHolder.fail(stack);
            }

            LivingEntity target = switch (mode) {
                case 0 -> player;
                case 1 -> getLookedAtEntity(player);
                default -> null;
            };

            if (target == null || (mode == 1 && target == player)) {
                player.displayClientMessage(Component.translatable("message.pathosyn.no_target"), true);
                level.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 1.0f, 1.0f);
                return InteractionResultHolder.fail(stack);
            }

            if (target.getHealth() >= target.getMaxHealth()) {
                player.displayClientMessage(Component.translatable("message.pathosyn.already_full"), true);
                level.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 1.0f, 1.0f);
                return InteractionResultHolder.fail(stack);
            }

            float healAmount = target.getMaxHealth() * 0.1f + 6f;
            target.heal(healAmount);

            int currentCharges = stack.getOrDefault(ModComponentTypes.getChargeCountComponent(), MAX_CHARGES);
            stack.set(ModComponentTypes.getChargeCountComponent(), currentCharges - 1);

            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);
            player.displayClientMessage(Component.translatable("message.pathosyn.healed", target.getName().getString()), true);

            // Add cooldown
            player.getCooldowns().addCooldown(this, 4);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    // 显示条
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }
    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        int value = ChargeCountComponent.get(stack);
        return Math.round(13.0F * value / ChargeCountComponent.MAX_CHARGES);
    }
    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00FF00;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && stack.getItem() instanceof HealingStaffItem) {
            int mode = stack.getOrDefault(StaffModeComponent.getModeComponent(), 0);
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(mode == 1 ? 1 : 0));
        }
    }
//  随机耐久
    @Override
    public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player) {
        super.onCraftedBy(stack, level, player);
        initializeRandomCharges(stack, level);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag flag) {
        int charges = ChargeCountComponent.get(stack);

        tooltip.add(Component.translatable("tooltip.pathosyn.charges", charges).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("")); // 空行

        tooltip.add(Component.translatable("tooltip.pathosyn.healing_staff.description.1").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.translatable("tooltip.pathosyn.healing_staff.description.2").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.literal("")); // 空行

        String shiftKey = Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString();
        String useKey = Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage().getString();
        tooltip.add(Component.translatable("tooltip.pathosyn.healing_staff.switch_mode", shiftKey, useKey).withStyle(ChatFormatting.DARK_GRAY));
    }
    @Override
    public @NotNull Component getName(ItemStack stack) {
        if (!stack.has(StaffModeComponent.getModeComponent())) {
            return Component.translatable(this.getDescriptionId());
        }

        int mode = stack.getOrDefault(StaffModeComponent.getModeComponent(), 0);
        return switch (mode) {
            case 0 -> Component.translatable("item.pathosyn.healing_staff.self");
            case 1 -> Component.translatable("item.pathosyn.healing_staff.other");
            default -> Component.translatable(this.getDescriptionId());
        };
    }

    private boolean isExhausted(ItemStack stack) {
        int charges = stack.getOrDefault(ModComponentTypes.getChargeCountComponent(), MAX_CHARGES);
        return charges <= 0;
    }

    private LivingEntity getLookedAtEntity(Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 reachVec = eyePos.add(viewVec.scale(5.0));
        AABB box = player.getBoundingBox().expandTowards(viewVec.scale(5.0)).inflate(1.0);

        List<Entity> entities = player.level().getEntities(player, box, e -> e instanceof LivingEntity && e != player && e.isPickable());
        Entity closest = null;
        double closestDist = 5.0 * 5.0;

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
    private void initializeRandomCharges(ItemStack stack, Level level) {
        if (!stack.has(ModComponentTypes.getChargeCountComponent())) {
            int value = 3 + level.random.nextInt(ChargeCountComponent.MAX_CHARGES - 2); // 3~10
            ChargeCountComponent.set(stack, value);
        }
    }
}