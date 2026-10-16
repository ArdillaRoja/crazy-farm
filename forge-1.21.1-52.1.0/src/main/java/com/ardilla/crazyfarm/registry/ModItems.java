package com.ardilla.crazyfarm.registry;

import com.ardilla.crazyfarm.CrazyFarm;
import com.ardilla.crazyfarm.item.VitiliumArmorItem;
import com.ardilla.crazyfarm.item.VitiliumArmorMaterial;
import com.ardilla.crazyfarm.item.VitiliumTier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
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

    // Vitilium: mineral, cadena de procesado y moneda
    public static final RegistryObject<Item> VITILIUM_ORE = blockItem("vitilium_ore", () -> ModBlocks.VITILIUM_ORE.get());
    public static final RegistryObject<Item> DEEPSLATE_VITILIUM_ORE = blockItem("deepslate_vitilium_ore", () -> ModBlocks.DEEPSLATE_VITILIUM_ORE.get());
    public static final RegistryObject<Item> VITILIUM_NUGGET = ITEMS.register("vitilium_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IMPURE_VITILIUM_BAR = ITEMS.register("impure_vitilium_bar", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VITILIUM_INGOT = ITEMS.register("vitilium_ingot", () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    /** Plantilla de la mejora a Vitilium en la mesa de herrería. */
    public static final RegistryObject<Item> VITILIUM_PLATE = ITEMS.register("vitilium_plate", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> VITCOIN = ITEMS.register("vitcoin", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    // Herramientas de Vitilium. Habilidades por etiqueta (ver ModTags).
    public static final RegistryObject<Item> VITILIUM_PICKAXE = ITEMS.register("vitilium_pickaxe",
            () -> new PickaxeItem(VitiliumTier.INSTANCE, gear().attributes(DiggerItem.createAttributes(VitiliumTier.INSTANCE, 1.0f, -2.8f))));
    public static final RegistryObject<Item> VITILIUM_AXE = ITEMS.register("vitilium_axe",
            () -> new AxeItem(VitiliumTier.INSTANCE, gear().attributes(DiggerItem.createAttributes(VitiliumTier.INSTANCE, 5.0f, -3.0f))));
    public static final RegistryObject<Item> VITILIUM_SHOVEL = ITEMS.register("vitilium_shovel",
            () -> new ShovelItem(VitiliumTier.INSTANCE, gear().attributes(DiggerItem.createAttributes(VitiliumTier.INSTANCE, 1.5f, -3.0f))));
    public static final RegistryObject<Item> VITILIUM_HOE = ITEMS.register("vitilium_hoe",
            () -> new HoeItem(VitiliumTier.INSTANCE, gear().attributes(DiggerItem.createAttributes(VitiliumTier.INSTANCE, -4.0f, 0.0f))));
    public static final RegistryObject<Item> VITILIUM_SWORD = ITEMS.register("vitilium_sword",
            () -> new SwordItem(VitiliumTier.INSTANCE, gear().attributes(SwordItem.createAttributes(VitiliumTier.INSTANCE, 3, -2.4f))));

    // Armadura de Vitilium
    /** Corona: visión nocturna permanente. */
    public static final RegistryObject<Item> VITILIUM_CROWN = ITEMS.register("vitilium_crown",
            () -> new VitiliumArmorItem(ArmorItem.Type.HELMET, armor(ArmorItem.Type.HELMET), null, 0.0, null, MobEffects.NIGHT_VISION));
    /** Pechera: reduce mucho el empuje. */
    public static final RegistryObject<Item> VITILIUM_CHESTPLATE = ITEMS.register("vitilium_chestplate",
            () -> new VitiliumArmorItem(ArmorItem.Type.CHESTPLATE, armor(ArmorItem.Type.CHESTPLATE),
                    Attributes.KNOCKBACK_RESISTANCE, 0.6, AttributeModifier.Operation.ADD_VALUE, null));
    /** Pantalones: resistencia al fuego. */
    public static final RegistryObject<Item> VITILIUM_LEGGINGS = ITEMS.register("vitilium_leggings",
            () -> new VitiliumArmorItem(ArmorItem.Type.LEGGINGS, armor(ArmorItem.Type.LEGGINGS), null, 0.0, null, MobEffects.FIRE_RESISTANCE));
    /** Botas: velocidad permanente. */
    public static final RegistryObject<Item> VITILIUM_BOOTS = ITEMS.register("vitilium_boots",
            () -> new VitiliumArmorItem(ArmorItem.Type.BOOTS, armor(ArmorItem.Type.BOOTS),
                    Attributes.MOVEMENT_SPEED, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, null));

    private static Item.Properties gear()
    {
        return new Item.Properties().fireResistant().rarity(Rarity.RARE);
    }

    private static Item.Properties armor(ArmorItem.Type type)
    {
        return gear().durability(type.getDurability(VitiliumArmorMaterial.DURABILITY_FACTOR));
    }

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
