package com.wuyouyu.pathosynmod.util;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public class ParticleUtil {
    /**
     * 在实体脚底生成绿色大号静止渐隐的法阵粒子环。
     * @param entity 目标实体
     * @param count 环形粒子数量
     * @param radius 环半径
     * @param yOffset 离地高度
     * @param size 粒子尺寸
     */
    public static void spawnGreenCircleParticles(Entity entity, int count, double radius, double yOffset, float size) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        double baseY = entity.getY() + yOffset;
        double x = entity.getX();
        double z = entity.getZ();
        DustParticleOptions greenParticle = new DustParticleOptions(new Vector3f(0.3F, 0.9F, 0.3F), size);
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double px = x + radius * Math.cos(angle);
            double pz = z + radius * Math.sin(angle);

            serverLevel.sendParticles(
                    ParticleTypes.ENCHANT,
                    px, baseY, pz,
                    1,
                    0, 0, 0, // 静止
                    0.0
            );
        }
    }
}