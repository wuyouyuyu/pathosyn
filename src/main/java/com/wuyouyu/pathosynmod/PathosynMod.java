package com.wuyouyu.pathosynmod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


import net.neoforged.bus.api.IEventBus;

import net.neoforged.fml.ModContainer;

import net.neoforged.fml.common.Mod;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import com.wuyouyu.pathosynmod.registry.ModItems;
import com.wuyouyu.pathosynmod.registry.ModTabs;


@Mod(PathosynMod.MODID)
public class PathosynMod {

    public static final String MODID = "pathosyn";
    private static final Logger LOGGER = LogUtils.getLogger();

    public PathosynMod(IEventBus modEventBus, ModContainer modContainer) {
        // 注册物品与创意标签
        ModItems.ITEMS.register(modEventBus);
        ModTabs.TABS.register(modEventBus);

        // 监听 setup 阶段（如需要）
        modEventBus.addListener(this::commonSetup);

        LOGGER.info("Pathosyn mod loaded.");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup called for Pathosyn");
    }
}
