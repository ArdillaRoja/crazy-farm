package com.ardilla.crazyfarm.registry;

import com.ardilla.crazyfarm.CrazyFarm;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrazyFarm.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + CrazyFarm.MODID))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.ONION.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.ONION_SEEDS.get());
                output.accept(ModItems.ONION.get());
                output.accept(ModItems.TURNIP_SEEDS.get());
                output.accept(ModItems.TURNIP.get());
                output.accept(ModItems.RADISH_SEEDS.get());
                output.accept(ModItems.RADISH.get());
                output.accept(ModItems.CASSAVA_CUTTING.get());
                output.accept(ModItems.CASSAVA.get());
                output.accept(ModItems.COOKED_CASSAVA.get());
                output.accept(ModItems.REJUVENATION_SPROUT.get());
                output.accept(ModItems.REJUVENATION_FRUIT.get());
            }).build());

    private ModCreativeTabs() {}
}
