package com.ardilla.crazyfarm.item;

import com.ardilla.crazyfarm.CrazyFarm;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import javax.annotation.Nullable;

/**
 * Pieza de armadura de Vitilium. Cada pieza puede llevar un atributo extra (empuje, velocidad...)
 * y/o un efecto permanente oculto que se renueva solo (ver ability.VitiliumEvents).
 */
public class VitiliumArmorItem extends ArmorItem
{
    private final Holder<Attribute> extraAttribute;
    private final AttributeModifier extraModifier;
    private final Holder<MobEffect> permanentEffect;
    private ItemAttributeModifiers cachedModifiers;

    public VitiliumArmorItem(Type type, Properties properties, @Nullable Holder<Attribute> extraAttribute,
                             double extraAmount, AttributeModifier.Operation extraOperation,
                             @Nullable Holder<MobEffect> permanentEffect)
    {
        super(VitiliumArmorMaterial.holder(), type, properties);
        this.extraAttribute = extraAttribute;
        this.extraModifier = extraAttribute == null ? null : new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(CrazyFarm.MODID, "vitilium_" + type.getName() + "_bonus"),
                extraAmount, extraOperation);
        this.permanentEffect = permanentEffect;
    }

    @Nullable
    public Holder<MobEffect> getPermanentEffect()
    {
        return permanentEffect;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers()
    {
        if (extraAttribute == null)
            return super.getDefaultAttributeModifiers();
        if (cachedModifiers == null)
        {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
            super.getDefaultAttributeModifiers().modifiers().forEach(e -> builder.add(e.attribute(), e.modifier(), e.slot()));
            builder.add(extraAttribute, extraModifier, EquipmentSlotGroup.bySlot(type.getSlot()));
            cachedModifiers = builder.build();
        }
        return cachedModifiers;
    }
}
