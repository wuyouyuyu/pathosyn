package com.wuyouyu.pathosynmod.datagen;

import com.wuyouyu.pathosynmod.PathosynMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;



@EventBusSubscriber(modid = PathosynMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PathosynDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        if (event.includeClient()) {



            generator.addProvider(true, new PathosynItemModelProvider(output, event.getExistingFileHelper()));
        }
    }
}