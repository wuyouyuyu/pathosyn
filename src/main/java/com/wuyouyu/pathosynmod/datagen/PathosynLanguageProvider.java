package com.wuyouyu.pathosynmod.datagen;

import com.wuyouyu.pathosynmod.PathosynMod;
import com.wuyouyu.pathosynmod.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PathosynLanguageProvider extends LanguageProvider {
    public PathosynLanguageProvider(PackOutput output) {
        super(output, PathosynMod.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ModItems.ITEMS.getEntries().forEach(entry -> {
            Item item = entry.get();
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id != null) {
                add(item.getDescriptionId(), toTitleCase(id.getPath().replace('_', ' ')));
            }
        });

        add("itemGroup." + PathosynMod.MODID + ".main", "Pathosyn");
    }

    private String toTitleCase(String input) {
        return Arrays.stream(input.split(" "))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" "));
    }
}