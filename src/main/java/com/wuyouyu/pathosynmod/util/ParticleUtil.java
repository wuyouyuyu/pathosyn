package com.wuyouyu.pathosynmod.util;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public class ParticleUtil {

    public static void spawnRuneRing(
            Entity entity, int runeCount, double radius, double yOffset,
            SimpleParticleType runeParticleType,
            int[] framePool // 只保留帧池
    ) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        double baseY = entity.getY() + yOffset;
        double x = entity.getX();
        double z = entity.getZ();
        RandomSource rand = serverLevel.getRandom();

        for (int i = 0; i < runeCount; i++) {
            double angle = 2 * Math.PI * i / runeCount;
            double px = x + radius * Math.cos(angle);
            double pz = z + radius * Math.sin(angle);
            int frameIndex = framePool.length > 0 ? framePool[rand.nextInt(framePool.length)] : 0;
            serverLevel.sendParticles(
                    runeParticleType, px, baseY, pz,
                    1,
                    frameIndex, // dx=frameIndex
                    0, 0,
                    0
            );
        }
    }
}