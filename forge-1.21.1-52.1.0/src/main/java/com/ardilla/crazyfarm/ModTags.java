package com.ardilla.crazyfarm;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Etiquetas propias. Las habilidades se activan por etiqueta: para que un ítem (de este mod, de un
 * addon o de un datapack) tenga una habilidad basta con agregarlo a la etiqueta correspondiente.
 */
public final class ModTags
{
    public static final class Items
    {
        /** El botín de bloques que se rompen con estos ítems se funde automáticamente. */
        public static final TagKey<Item> AUTO_SMELT = tag("abilities/auto_smelt");
        /** Ítems del botín que la habilidad de arriba puede fundir. */
        public static final TagKey<Item> AUTO_SMELT_INPUTS = tag("auto_smelt_inputs");
        /** Rompen todo el árbol conectado (agachado = un solo tronco). */
        public static final TagKey<Item> TREE_FELLING = tag("abilities/tree_felling");
        /** Rompen un área 3x3 perpendicular a la cara golpeada (agachado = un solo bloque). */
        public static final TagKey<Item> AREA_DIG = tag("abilities/area_dig");
        /** Aran un área 3x3 (agachado = un solo bloque). */
        public static final TagKey<Item> AREA_TILL = tag("abilities/area_till");
        /** Probabilidad de golpe crítico aumentado. */
        public static final TagKey<Item> CRITICAL_HIT = tag("abilities/critical_hit");
        /** Recuperan vida con cada golpe. */
        public static final TagKey<Item> LIFE_STEAL = tag("abilities/life_steal");

        private static TagKey<Item> tag(String name)
        {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CrazyFarm.MODID, name));
        }

        private Items() {}
    }

    private ModTags() {}
}
