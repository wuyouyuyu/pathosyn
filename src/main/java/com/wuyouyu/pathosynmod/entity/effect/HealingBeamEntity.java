package com.wuyouyu.pathosynmod.entity.effect;

import com.wuyouyu.pathosynmod.item.custom.HealingStaffItem;
import com.wuyouyu.pathosynmod.registry.ModComponentTypes;

import com.wuyouyu.pathosynmod.registry.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

    public void setDirection(Vec3 dir) {
        Vec3 direction = dir.normalize().scale(SPEED);
        this.setDeltaMovement(direction);
    }

    public void setSourceStack(ItemStack stack) {
        this.sourceStack = stack.copy();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.origin == null) {
            this.origin = this.position();
        }

        if (this.level().isClientSide) {
            spawnTrailParticles();
        }

        if (!this.level().isClientSide) {
            this.setPos(this.position().add(this.getDeltaMovement()));

            //  射程限制
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

    private void spawnTrailParticles() {
        this.level().addParticle(ModParticles.HEALING_BEAM.get(), getX(), getY(), getZ(), 0.01, 0.02, 0.01);

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

            if (level() instanceof ServerLevelAccessor serverLevel) {
                serverLevel.getLevel().sendParticles(ParticleTypes.HEART,
                        target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                        5, 0.2, 0.3, 0.2, 0.01);
            }

            level().playSound(null, target.blockPosition(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);

            player.displayClientMessage(
                    Component.translatable("message.pathosyn.healed", target.getName().getString()), true);

            this.discard();
        }
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.@NotNull Builder builder) {
        // no synced data needed
    }

    @Override
    protected void readAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag tag) {}
}
