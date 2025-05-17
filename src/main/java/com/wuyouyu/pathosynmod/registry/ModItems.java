package com.wuyouyu.pathosynmod.registry;

import com.wuyouyu.pathosynmod.PathosynMod;
import com.wuyouyu.pathosynmod.item.custom.HealingStaffItem;
import net.minecraft.world.item.Item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PathosynMod.MODID);

    public static final DeferredItem<Item> HEALING_STAFF =
            registerItem(() -> new HealingStaffItem(new Item.Properties()));



    private static DeferredItem<Item> registerItem(Supplier<Item> supplier) {
        return ITEMS.register("healing_staff", supplier);
    }
}
