package com.wuyouyu.pathosynmod.item.custom;


import com.wuyouyu.pathosynmod.component.ChargeCountComponent;
import com.wuyouyu.pathosynmod.component.StaffModeComponent;

import com.wuyouyu.pathosynmod.entity.effect.HealingBeamEntity;
import com.wuyouyu.pathosynmod.registry.ModComponentTypes;
import com.wuyouyu.pathosynmod.registry.ModEntities;

import com.wuyouyu.pathosynmod.registry.ModParticles;
import com.wuyouyu.pathosynmod.util.ParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;

import net.minecraft.core.particles.ParticleTypes;
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
                player.displayClientMessage(Component.translatable("message.pathosyn.switched_mode", next), true);
                return InteractionResultHolder.sidedSuccess(stack, false);
            }

            // 判断充能是否用尽
            if (ChargeCountComponent.isExhausted(stack)) {
                player.displayClientMessage(Component.translatable("message.pathosyn.exhausted"), true);
                return InteractionResultHolder.fail(stack);
            }

            player.getCooldowns().addCooldown(this, 20);

            int mode = stack.getOrDefault(StaffModeComponent.getModeComponent(), 0);
            if (mode == 0) {
                // 模式 0：立即治疗自己
                float healAmount = player.getMaxHealth() * 0.1f + 6f;
                player.heal(healAmount);

                // 充能 -1
                ChargeCountComponent.consume(stack);

                // 法阵符文粒子（推荐用工具类，可多物品复用）
                ParticleUtil.spawnGreenCircleParticles(
                        player,     // 以玩家为中心
                        24,         // 环形点数（推荐 24~32）
                        1.2,        // 半径（更大更明显）
                        0.1,        // 离地高度（略高于地表）
                        2.5F        // 粒子尺寸（大法阵）
                );



                level.playSound(null, player.blockPosition(),
                        SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);

                player.displayClientMessage(
                        Component.translatable("message.pathosyn.healed", player.getName().getString()), true);
                return InteractionResultHolder.sidedSuccess(stack, false);
            }

            // 模式 1：发射治疗光束
            // 1. 计算自定义起点
            Vec3 eyePos = new Vec3(player.getX(), player.getEyeY(), player.getZ());
            Vec3 rightOffset = player.getViewVector(1.0F).cross(new Vec3(0, 1, 0)).normalize().scale(0.3); // 右偏
            Vec3 downOffset = new Vec3(0, -0.2, 0); // 下偏
            Vec3 start = eyePos.add(rightOffset).add(downOffset);

            // 2. 计算准心目标点（如无目标实体就正前方 maxRange 距离）
            double maxRange = 8.0;
            Vec3 look = player.getLookAngle();
            Vec3 center = eyePos.add(look.scale(maxRange));

            // 3. 方向（瞄准准心/目标中心）
            Vec3 direction = center.subtract(start).normalize();

            // 4. 创建并发射实体
            HealingBeamEntity beam = new HealingBeamEntity(ModEntities.HEALING_BEAM.get(), level);
            beam.setOwner(player);
            beam.setDirection(direction, 0);
            beam.setSourceStack(stack);
            beam.setPos(start.x, start.y, start.z);
            level.addFreshEntity(beam);

            // 5. 扣除充能（工具类封装）
            ChargeCountComponent.consume(stack);

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
        int value = ChargeCountComponent.get(stack); // 用工具方法
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
        int charges = ChargeCountComponent.get(stack);

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
        if (ChargeCountComponent.get(stack) == 0) { // 判断当前是否有充能
            int value = 3 + level.random.nextInt(MAX_CHARGES - 2);
            ChargeCountComponent.set(stack, value);
        }
    }
}
