package com.ardilla.crazyfarm.farming;

import net.minecraft.util.RandomSource;

/** Cuántos frutos extra da un cultivo maduro. El total por cosecha va de 1 a 4. */
public final class CropYield
{
    public static final int MAX_EXTRA = 3;

    /** Probabilidad de cada tirada extra sin riego. */
    public static final float BASE_CHANCE = 0.25f;
    /** Lo que sube esa probabilidad por cada nivel de riego. */
    public static final float CHANCE_PER_IRRIGATION_LEVEL = 0.12f;
    public static final float MAX_CHANCE = 0.9f;

    /**
     * Tiradas sucesivas: cada fruto extra exige haber conseguido el anterior, así que
     * 4 frutos es mucho más raro que 2. Sin riego: 1 fruto 75%, 2 19%, 3 5%, 4 1.5%.
     */
    public static int rollExtra(RandomSource random, int irrigationLevel)
    {
        float chance = Math.min(MAX_CHANCE, BASE_CHANCE + CHANCE_PER_IRRIGATION_LEVEL * irrigationLevel);
        int extra = 0;
        while (extra < MAX_EXTRA && random.nextFloat() < chance)
            extra++;
        return extra;
    }

    private CropYield() {}
}
