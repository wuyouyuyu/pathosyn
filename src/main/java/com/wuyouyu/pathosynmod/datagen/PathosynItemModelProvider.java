package com.wuyouyu.pathosynmod.datagen;


import com.wuyouyu.pathosynmod.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class PathosynItemModelProvider extends ItemModelProvider {
    public PathosynItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        // 路径生成为 assetss/pathosyn/models/item/
        super(output, "assetss/pathosyn", helper);
    }

    @Override
    protected void registerModels() {
        ModItems.ITEMS.getEntries().forEach(entry -> {
            Item item = entry.get();
            handheldItem(item);

        });
    }
}