package com.ardilla.crazyfarm.farming;

import com.ardilla.crazyfarm.block.IrrigationDeviceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Reglas del dispositivo de riego. Todos los valores están indexados por nivel (0 = sin riego,
 * 1 a 4 = nivel del dispositivo) y son valores iniciales que se ajustan probando.
 *
 * Área que cubre un dispositivo: un cuadrado de lado 2r+1 alrededor de él y desde 2 bloques
 * por debajo hasta 1 por encima. Es limitada a propósito para no causar lag.
 */
public final class Irrigation
{
    public static final int MAX_LEVEL = 4;

    /** Radio horizontal. */
    public static final int[] RADIUS = {0, 4, 5, 6, 7};
    /** Velocidad de crecimiento respecto a lo normal. */
    public static final double[] GROWTH_SPEED = {1.0, 1.0, 1.5, 2.0, 3.0};
    /** Probabilidad de que un cultivo mute al madurar. */
    public static final double[] MUTATION_CHANCE = {0.0, 0.0, 0.03, 0.06, 0.10};

    /** Cada cuántos ticks revisa su área un dispositivo. */
    public static final int SCAN_INTERVAL = 40;
    public static final int AREA_DOWN = 2;
    public static final int AREA_UP = 1;

    /** Zonas de agua activas por mundo y posición del dispositivo. Solo se usa en el hilo del servidor. */
    private static final Map<Level, Map<BlockPos, AABBTicket>> WATER_TICKETS = new WeakHashMap<>();

    /**
     * Mantiene registrada la zona de agua del dispositivo. Forge borra los tickets al descargarse
     * el chunk, por eso se revisa en cada trabajo periódico y se vuelve a crear si hace falta.
     */
    public static void ensureWaterTicket(ServerLevel level, BlockPos device, int tier)
    {
        Map<BlockPos, AABBTicket> tickets = WATER_TICKETS.computeIfAbsent(level, l -> new HashMap<>());
        AABBTicket ticket = tickets.get(device);
        if (ticket == null || !ticket.isValid())
        {
            int r = RADIUS[tier];
            AABB area = new AABB(device.getX() - r, device.getY() - AREA_DOWN, device.getZ() - r,
                    device.getX() + r + 1, device.getY() + AREA_UP + 1, device.getZ() + r + 1);
            tickets.put(device.immutable(), FarmlandWaterManager.addAABBTicket(level, area));
        }
    }

    /** Quita la zona de agua cuando se rompe el dispositivo. */
    public static void removeWaterTicket(Level level, BlockPos device)
    {
        Map<BlockPos, AABBTicket> tickets = WATER_TICKETS.get(level);
        if (tickets != null)
        {
            AABBTicket ticket = tickets.remove(device);
            if (ticket != null)
                ticket.invalidate();
        }
    }

    /** Nivel del mejor dispositivo que cubre una posición (0 si ninguno). */
    public static int levelAt(Level level, BlockPos pos)
    {
        int best = 0;
        int maxR = RADIUS[MAX_LEVEL];
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        // Un dispositivo en (dx, dy, dz) cubre la posición si ella está a AREA_DOWN..AREA_UP de su altura.
        for (int dy = -AREA_UP; dy <= AREA_DOWN; dy++)
            for (int dx = -maxR; dx <= maxR; dx++)
                for (int dz = -maxR; dz <= maxR; dz++)
                {
                    p.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (!level.isLoaded(p))
                        continue;
                    if (level.getBlockState(p).getBlock() instanceof IrrigationDeviceBlock device)
                    {
                        int tier = device.getTier();
                        if (tier > best && Math.abs(dx) <= RADIUS[tier] && Math.abs(dz) <= RADIUS[tier])
                            best = tier;
                    }
                }
        return best;
    }

    public static double mutationChance(int tier)
    {
        return MUTATION_CHANCE[Math.max(0, Math.min(MAX_LEVEL, tier))];
    }

    /**
     * Trabajo periódico de un dispositivo: mantiene su zona de agua y acelera los cultivos.
     * La aceleración da tiradas aleatorias extra a cada cultivo, con la misma probabilidad con la
     * que el juego les da las normales (según el gamerule randomTickSpeed), por la velocidad menos 1.
     */
    public static void irrigateArea(ServerLevel level, BlockPos device, int tier, RandomSource random)
    {
        ensureWaterTicket(level, device, tier);
        int r = RADIUS[tier];
        double randomTickSpeed = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        double extraChance = Math.min(1.0, SCAN_INTERVAL * randomTickSpeed / 4096.0 * (GROWTH_SPEED[tier] - 1.0));

        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int dy = -AREA_DOWN; dy <= AREA_UP; dy++)
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++)
                {
                    p.set(device.getX() + dx, device.getY() + dy, device.getZ() + dz);
                    if (!level.isLoaded(p))
                        continue;
                    BlockState state = level.getBlockState(p);
                    if (state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state)
                            && random.nextDouble() < extraChance)
                        state.randomTick(level, p.immutable(), random);
                }
    }

    private Irrigation() {}
}
