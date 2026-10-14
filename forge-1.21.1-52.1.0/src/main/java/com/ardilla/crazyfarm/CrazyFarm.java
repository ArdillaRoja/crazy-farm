package com.ardilla.crazyfarm;

import com.ardilla.crazyfarm.registry.ModBlocks;
import com.ardilla.crazyfarm.registry.ModCreativeTabs;
import com.ardilla.crazyfarm.registry.ModItems;
import com.ardilla.crazyfarm.registry.ModLootModifiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
    }
}
