package com.wuyouyu.pathosynmod.entity.effect;

import com.wuyouyu.pathosynmod.item.custom.HealingStaffItem;
import com.wuyouyu.pathosynmod.registry.ModComponentTypes;


import com.wuyouyu.pathosynmod.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class HealingBeamEntity extends Entity {

    private static final int LIFESPAN_TICKS = 40;
    private static final double SPEED = 0.5;
    private static final double MAX_RANGE = 8.0;

    private Player owner;
    private ItemStack sourceStack = ItemStack.EMPTY;
    private int age;
    private Vec3 origin;

    public HealingBeamEntity(EntityType<? extends HealingBeamEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setOwner(Player player) {
        this.owner = player;
    }

    /**
     * 通过 yawDegreesOffset 直接调整发射方向，正值左偏，负值右偏
     */
    public void setDirection(Vec3 dir, double yawDegreesOffset) {
        double yawRad = Math.toRadians(yawDegreesOffset);
        double cos = Math.cos(yawRad);
        double sin = Math.sin(yawRad);

        // 水平旋转
        double newX = dir.x * cos - dir.z * sin;
        double newZ = dir.x * sin + dir.z * cos;
        Vec3 newDir = new Vec3(newX, dir.y, newZ);

        Vec3 direction = newDir.normalize().scale(SPEED);
        this.setDeltaMovement(direction);
    }

    public void setSourceStack(ItemStack stack) {
        this.sourceStack = stack.copy();
    }

    @Override
    public void tick() {
        super.tick();

        // 1. 尾迹粒子效果：数量随 age 递增（最大 8 个）
        int maxParticles = 8;
        int particles = Math.max(1, (int)(maxParticles * (age / (float)LIFESPAN_TICKS)));
        // 二次递增: int particles = Math.max(1, (int)(maxParticles * Math.pow(age / (float)LIFESPAN_TICKS, 2)));

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ModParticles.HEALING_BEAM.get(),
                    this.getX(), this.getY(), this.getZ(),
                    particles,                          // 递增粒子数
                    0.05, 0.05, 0.05, 0.01              // 偏移和速度参数
            );
        }

        if (this.origin == null) {
            this.origin = this.position();
        }

        if (!this.level().isClientSide) {
            this.setPos(this.position().add(this.getDeltaMovement()));

            if (this.origin.distanceTo(this.position()) >= MAX_RANGE) {
                this.discard();
                return;
            }

            HitResult result = ProjectileUtil.getHitResultOnMoveVector(this,
                    e -> e instanceof LivingEntity && e != this.owner && e.isPickable());

            if (result.getType() == HitResult.Type.ENTITY && result instanceof EntityHitResult entityHit) {
                onHitEntity(entityHit);
            }

            if (++age > LIFESPAN_TICKS) {
                this.discard();
            }
        }
    }

    private void onHitEntity(EntityHitResult result) {
        Entity hit = result.getEntity();
        if (!(owner instanceof Player player)) return;

        if (hit instanceof LivingEntity target) {
            ItemStack held = player.getMainHandItem();
            if (!(held.getItem() instanceof HealingStaffItem)) return;

            int charges = held.getOrDefault(ModComponentTypes.getChargeCountComponent(), HealingStaffItem.MAX_CHARGES);
            if (charges <= 0 || target.getHealth() >= target.getMaxHealth()) return;

            float healAmount = target.getMaxHealth() * 0.1f + 6f;
            target.heal(healAmount);
            held.set(ModComponentTypes.getChargeCountComponent(), charges - 1);

            if (this.level() instanceof ServerLevel serverLevel) {
                Vec3 center = new Vec3(
                        target.getX(),
                        target.getY() + target.getBbHeight(),
                        target.getZ()
                );

                RandomSource random = serverLevel.getRandom();
                int particleCount = 2 + random.nextInt(3);
                double radius = 0.4;

                for (int i = 0; i < particleCount; i++) {
                    double angle = i * (2 * Math.PI / particleCount);
                    double offsetX = radius * Math.cos(angle);
                    double offsetZ = radius * Math.sin(angle);
                    double offsetY = (random.nextDouble() - 0.5) * 0.2;  // 小范围起伏

                    serverLevel.sendParticles(
                            ModParticles.HEALING_BEAM_HIT.get(),
                            center.x + offsetX,
                            center.y + offsetY,
                            center.z + offsetZ,
                            1, 0, 0, 0, 0
                    );
                }

                serverLevel.playSound(null, target.blockPosition(),
                        SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            player.displayClientMessage(
                    Component.translatable("message.pathosyn.healed", target.getName().getString()), true);

            this.discard();
        }
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.@NotNull Builder builder) {}

    @Override
    protected void readAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag tag) {}
}
