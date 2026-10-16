package com.ardilla.crazyfarm.registry;

import com.ardilla.crazyfarm.CrazyFarm;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrazyFarm.MODID);

    // Cebolla
    public static final RegistryObject<Item> ONION_SEEDS = seeds("onion_seeds", () -> ModBlocks.ONION_CROP.get());
    public static final RegistryObject<Item> ONION = food("onion", 3, 0.4f);

    // Nabo
    public static final RegistryObject<Item> TURNIP_SEEDS = seeds("turnip_seeds", () -> ModBlocks.TURNIP_CROP.get());
    public static final RegistryObject<Item> TURNIP = food("turnip", 3, 0.4f);

    // Rábano
    public static final RegistryObject<Item> RADISH_SEEDS = seeds("radish_seeds", () -> ModBlocks.RADISH_CROP.get());
    public static final RegistryObject<Item> RADISH = food("radish", 2, 0.3f);

    // Yuca: cruda marea y puede envenenar; cocida alimenta mucho
    public static final RegistryObject<Item> CASSAVA_CUTTING = seeds("cassava_cutting", () -> ModBlocks.CASSAVA_CROP.get());
    public static final RegistryObject<Item> CASSAVA = ITEMS.register("cassava",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(2).saturationModifier(0.3f)
                    .effect(new MobEffectInstance(MobEffects.CONFUSION, 200), 1.0f)
                    .effect(new MobEffectInstance(MobEffects.POISON, 100), 0.4f)
                    .build())));
    public static final RegistryObject<Item> COOKED_CASSAVA = food("cooked_cassava", 8, 0.8f);

    // Fruto del Rejuvenecimiento. El brote solo se consigue en creativo o por comandos.
    public static final RegistryObject<Item> REJUVENATION_SPROUT = ITEMS.register("rejuvenation_sprout",
            () -> new ItemNameBlockItem(ModBlocks.REJUVENATION_PLANT.get(), new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> REJUVENATION_FRUIT = ITEMS.register("rejuvenation_fruit",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(2).saturationModifier(0.3f).alwaysEdible()
                    .effect(new MobEffectInstance(MobEffects.REGENERATION, 200), 1.0f)
                    .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200), 1.0f)
                    .build())));

    // Versiones gigantes de los cultivos mutantes (el granjero las pagará bien)
    public static final RegistryObject<Item> GIANT_ONION = ITEMS.register("giant_onion", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> GIANT_TURNIP = ITEMS.register("giant_turnip", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> GIANT_RADISH = ITEMS.register("giant_radish", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> GIANT_CASSAVA = ITEMS.register("giant_cassava", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    // Dispositivos de riego
    public static final RegistryObject<Item> IRRIGATION_DEVICE_1 = blockItem("irrigation_device_1", () -> ModBlocks.IRRIGATION_DEVICE_1.get());
    public static final RegistryObject<Item> IRRIGATION_DEVICE_2 = blockItem("irrigation_device_2", () -> ModBlocks.IRRIGATION_DEVICE_2.get());
    public static final RegistryObject<Item> IRRIGATION_DEVICE_3 = blockItem("irrigation_device_3", () -> ModBlocks.IRRIGATION_DEVICE_3.get());
    public static final RegistryObject<Item> IRRIGATION_DEVICE_4 = blockItem("irrigation_device_4", () -> ModBlocks.IRRIGATION_DEVICE_4.get());

    private static RegistryObject<Item> blockItem(String name, Supplier<Block> block)
    {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static RegistryObject<Item> seeds(String name, Supplier<Block> crop)
    {
        return ITEMS.register(name, () -> new ItemNameBlockItem(crop.get(), new Item.Properties()));
    }

    private static RegistryObject<Item> food(String name, int nutrition, float saturation)
    {
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build())));
    }

    private ModItems() {}
}
