package com.wuyouyu.pathosynmod.item.custom;


import com.wuyouyu.pathosynmod.component.StaffModeComponent;

import com.wuyouyu.pathosynmod.entity.effect.HealingBeamEntity;
import com.wuyouyu.pathosynmod.registry.ModComponentTypes;
import com.wuyouyu.pathosynmod.registry.ModEntities;

import com.wuyouyu.pathosynmod.registry.ModParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;

import net.minecraft.network.chat.Component;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.TooltipFlag;

import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.ServerLevelAccessor;
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
                player.displayClientMessage(Component.translatable("message.pathosyn.switched_mode", next), true);
                return InteractionResultHolder.sidedSuccess(stack, false);
            }

            int currentCharges = stack.getOrDefault(ModComponentTypes.getChargeCountComponent(), MAX_CHARGES);
            if (currentCharges <= 0) {
                player.displayClientMessage(Component.translatable("message.pathosyn.exhausted"), true);
                return InteractionResultHolder.fail(stack); //  不触发冷却
            }

// ✅ 只有在可使用时才设置冷却
            player.getCooldowns().addCooldown(this, 20);

            int mode = stack.getOrDefault(StaffModeComponent.getModeComponent(), 0);
            if (mode == 0) {
                // 模式 0：立即治疗自己
                float healAmount = player.getMaxHealth() * 0.1f + 6f;
                player.heal(healAmount);
                stack.set(ModComponentTypes.getChargeCountComponent(), currentCharges - 1);

                if (level instanceof ServerLevelAccessor serverLevel) {
                    serverLevel.getLevel().sendParticles(
                            ModParticles.HEALING_BEAM_HIT.get(),
                            player.getX(),
                            player.getY() + player.getBbHeight() / 2,
                            player.getZ(),
                            6,                      // 粒子数量略多，给出“爆发感”
                            0.2, 0.2, 0.2,          // 扩散范围 x/y/z
                            0.01                    // 漂浮速度
                    );
                }

                level.playSound(null, player.blockPosition(),
                        SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);

                player.displayClientMessage(
                        Component.translatable("message.pathosyn.healed", player.getName().getString()), true);
                return InteractionResultHolder.sidedSuccess(stack, false);
            }

            // 模式 1：发射粒子束实体
            stack.set(ModComponentTypes.getChargeCountComponent(), currentCharges - 1);

            HealingBeamEntity beam = new HealingBeamEntity(ModEntities.HEALING_BEAM.get(), level);
            beam.setOwner(player);
            beam.setDirection(player.getLookAngle());
            beam.setSourceStack(stack);
            beam.setPos(player.getX(), player.getEyeY(), player.getZ());
            level.addFreshEntity(beam);

            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        return InteractionResultHolder.sidedSuccess(stack, true);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        int value = stack.getOrDefault(ModComponentTypes.getChargeCountComponent(), MAX_CHARGES);
        return Math.round(13.0F * value / MAX_CHARGES);
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

    @Override
    public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player) {
        super.onCraftedBy(stack, level, player);
        initializeRandomCharges(stack, level);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag flag) {
        int charges = stack.getOrDefault(ModComponentTypes.getChargeCountComponent(), MAX_CHARGES);

        tooltip.add(Component.translatable("tooltip.pathosyn.charges", charges).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("tooltip.pathosyn.healing_staff.description.1"));
        tooltip.add(Component.translatable("tooltip.pathosyn.healing_staff.description.2"));
        tooltip.add(Component.literal(""));

        String shiftKey = Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString();
        String useKey = Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage().getString();
        tooltip.add(Component.translatable("tooltip.pathosyn.healing_staff.switch_mode", shiftKey, useKey));
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

    private void initializeRandomCharges(ItemStack stack, Level level) {
        if (!stack.has(ModComponentTypes.getChargeCountComponent())) {
            int value = 3 + level.random.nextInt(MAX_CHARGES - 2);
            stack.set(ModComponentTypes.getChargeCountComponent(), value);
        }
    }
}
