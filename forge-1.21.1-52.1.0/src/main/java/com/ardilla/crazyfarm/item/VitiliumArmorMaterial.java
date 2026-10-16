package com.ardilla.crazyfarm.item;

import com.ardilla.crazyfarm.CrazyFarm;
import com.ardilla.crazyfarm.registry.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Material de la armadura de Vitilium. Los ítems se registran antes que los materiales de armadura,
 * así que el material se registra aquí, la primera vez que un ítem de armadura lo pide.
 */
public final class VitiliumArmorMaterial
{
    /** Factor de durabilidad. Netherita: 37. */
    public static final int DURABILITY_FACTOR = 100;

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CrazyFarm.MODID, "vitilium");
    private static Holder<ArmorMaterial> holder;

    public static synchronized Holder<ArmorMaterial> holder()
    {
        if (holder == null)
        {
            ResourceKey<ArmorMaterial> key = ResourceKey.create(Registries.ARMOR_MATERIAL, ID);
            if (BuiltInRegistries.ARMOR_MATERIAL.containsKey(ID))
            {
                holder = BuiltInRegistries.ARMOR_MATERIAL.getHolderOrThrow(key);
            }
            else
            {
                Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
                // Netherita: 3 / 8 / 6 / 3 (casco, pechera, pantalones, botas)
                defense.put(ArmorItem.Type.HELMET, 4);
                defense.put(ArmorItem.Type.CHESTPLATE, 9);
                defense.put(ArmorItem.Type.LEGGINGS, 7);
                defense.put(ArmorItem.Type.BOOTS, 4);
                defense.put(ArmorItem.Type.BODY, 11);
                ArmorMaterial material = new ArmorMaterial(
                        defense,
                        25,                                      // encantabilidad (netherita: 15)
                        SoundEvents.ARMOR_EQUIP_NETHERITE,
                        () -> Ingredient.of(ModItems.VITILIUM_INGOT.get()),
                        List.of(new ArmorMaterial.Layer(ID)),
                        4.0f,                                    // dureza (netherita: 3)
                        0.15f);                                  // resistencia al empuje (netherita: 0.1)
                holder = Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, ID, material);
            }
        }
        return holder;
    }

    private VitiliumArmorMaterial() {}
}
