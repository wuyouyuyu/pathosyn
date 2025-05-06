package com.wuyouyu.pathosynmod.datagen;

import com.wuyouyu.pathosynmod.PathosynMod;
import com.wuyouyu.pathosynmod.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class PathosynItemModelProvider extends ItemModelProvider {
    public PathosynItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, PathosynMod.MODID, helper);
    }

    @Override
    protected void registerModels() {
        ModItems.ITEMS.getEntries().forEach(entry -> {
            Item item = entry.get();
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id != null) {
                handheldItem(item);
            }
        });
    }
}