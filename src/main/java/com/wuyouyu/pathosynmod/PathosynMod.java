package com.wuyouyu.pathosynmod;




import com.wuyouyu.pathosynmod.client.PathosynClient;
import com.wuyouyu.pathosynmod.registry.*;

import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


import net.neoforged.bus.api.IEventBus;

import net.neoforged.fml.ModContainer;

import net.neoforged.fml.common.Mod;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;


@Mod(PathosynMod.MODID)
public class PathosynMod {

    public static final String MODID = "pathosyn";
    private static final Logger LOGGER = LogUtils.getLogger();

    public PathosynMod(IEventBus modEventBus, ModContainer modContainer) {
        // 注册物品与创造标签
        ModItems.ITEMS.register(modEventBus);
        ModTabs.TABS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModComponentTypes.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);



        // 通用 setup
        modEventBus.addListener(this::commonSetup);

        // ✅ 客户端专用渲染注册
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(PathosynClient::onClientSetup);
        }
        // 监听事件

        LOGGER.info("Pathosyn mod loaded.");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup called for Pathosyn");
    }
}
