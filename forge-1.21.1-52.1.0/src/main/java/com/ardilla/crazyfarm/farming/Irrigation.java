package com.ardilla.crazyfarm.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Nivel del dispositivo de riego que afecta a una posición (0 = ninguno, 1 a 4 = nivel).
 * Provisional: hasta la etapa 2 de la hoja de ruta siempre devuelve 0. Cuando exista el
 * dispositivo de riego, solo hay que implementar este método y todos los cultivos lo usan.
 */
public final class Irrigation
{
    public static final int MAX_LEVEL = 4;

    public static int levelAt(Level level, BlockPos pos)
    {
        return 0;
    }

    private Irrigation() {}
}
