package com.ardilla.crazyfarm.item;

import com.ardilla.crazyfarm.registry.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/** Material de las herramientas de Vitilium: mejor que la netherita en todo. Valores iniciales. */
public enum VitiliumTier implements Tier
{
    INSTANCE;

    /** Netherita: 2031 usos. */
    private static final int USES = 6000;
    /** Netherita: 9.0. */
    private static final float SPEED = 12.0f;
    /** Netherita: 4.0. */
    private static final float ATTACK_DAMAGE_BONUS = 5.0f;
    /** Netherita: 15. */
    private static final int ENCHANTMENT_VALUE = 25;

    @Override
    public int getUses()
    {
        return USES;
    }

    @Override
    public float getSpeed()
    {
        return SPEED;
    }

    @Override
    public float getAttackDamageBonus()
    {
        return ATTACK_DAMAGE_BONUS;
    }

    /** Igual que la netherita: ningún bloque le niega el botín. */
    @Override
    public TagKey<Block> getIncorrectBlocksForDrops()
    {
        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }

    @Override
    public int getEnchantmentValue()
    {
        return ENCHANTMENT_VALUE;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return Ingredient.of(ModItems.VITILIUM_INGOT.get());
    }
}
