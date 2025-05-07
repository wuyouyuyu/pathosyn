package com.wuyouyu.pathosynmod.registry;

import com.wuyouyu.pathosynmod.PathosynMod;
import com.wuyouyu.pathosynmod.item.custom.HealingStaffItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PathosynMod.MODID);

    public static final DeferredItem<Item> HEALING_STAFF =
            registerItem("healing_staff", () -> new HealingStaffItem(new Item.Properties().durability(10)));




    private static DeferredItem<Item> registerItem(String name, Supplier<Item> supplier) {
        return ITEMS.register(name, supplier);
    }
}
