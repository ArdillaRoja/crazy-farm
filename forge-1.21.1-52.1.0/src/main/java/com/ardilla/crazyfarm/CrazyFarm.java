package com.ardilla.crazyfarm;

import com.ardilla.crazyfarm.registry.ModBlocks;
import com.ardilla.crazyfarm.registry.ModCreativeTabs;
import com.ardilla.crazyfarm.registry.ModItems;
import com.ardilla.crazyfarm.registry.ModLootModifiers;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CrazyFarm.MODID)
public class CrazyFarm
{
    public static final String MODID = "crazyfarm";

    public CrazyFarm(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(CrazyFarm::registerCompostables);
    }

    /** Mismas probabilidades que las verduras y semillas de vanilla. */
    private static void registerCompostables()
    {
        float seeds = 0.3f;
        float vegetables = 0.65f;
        ComposterBlock.COMPOSTABLES.put(ModItems.ONION_SEEDS.get(), seeds);
        ComposterBlock.COMPOSTABLES.put(ModItems.TURNIP_SEEDS.get(), seeds);
        ComposterBlock.COMPOSTABLES.put(ModItems.RADISH_SEEDS.get(), seeds);
        ComposterBlock.COMPOSTABLES.put(ModItems.CASSAVA_CUTTING.get(), seeds);
        ComposterBlock.COMPOSTABLES.put(ModItems.ONION.get(), vegetables);
        ComposterBlock.COMPOSTABLES.put(ModItems.TURNIP.get(), vegetables);
        ComposterBlock.COMPOSTABLES.put(ModItems.RADISH.get(), vegetables);
        ComposterBlock.COMPOSTABLES.put(ModItems.CASSAVA.get(), vegetables);
        ComposterBlock.COMPOSTABLES.put(ModItems.COOKED_CASSAVA.get(), 0.85f);
        ComposterBlock.COMPOSTABLES.put(ModItems.REJUVENATION_FRUIT.get(), vegetables);
    }
}
