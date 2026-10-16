package com.ardilla.crazyfarm.block;

import com.ardilla.crazyfarm.farming.Irrigation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Dispositivo de riego de nivel 1 a 4. No necesita agua ni recargas. Cada cierto tiempo revisa su
 * área (ver {@link Irrigation}) con un tick programado, sin entidad de bloque.
 */
public class IrrigationDeviceBlock extends Block
{
    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 11.0, 12.0);

    private final int tier;

    public IrrigationDeviceBlock(Properties properties, int tier)
    {
        super(properties);
        this.tier = tier;
    }

    public int getTier()
    {
        return tier;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston)
    {
        // El primer tick se reparte al azar para que varios dispositivos no trabajen en el mismo instante.
        if (!level.isClientSide && !oldState.is(state.getBlock()))
            level.scheduleTick(pos, this, 1 + level.random.nextInt(Irrigation.SCAN_INTERVAL));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (!level.isClientSide && !state.is(newState.getBlock()))
            Irrigation.removeWaterTicket(level, pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        Irrigation.irrigateArea(level, pos, tier, random);
        level.scheduleTick(pos, this, Irrigation.SCAN_INTERVAL);
    }

    /** Efecto visual de riego: gotas que salen de la cabeza del dispositivo. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.75;
        double z = pos.getZ() + 0.5;
        int drops = 1 + tier / 2;
        for (int i = 0; i < drops; i++)
        {
            double angle = random.nextDouble() * Math.PI * 2.0;
            level.addParticle(ParticleTypes.SPLASH, x, y, z, Math.cos(angle) * 0.2, 0.15, Math.sin(angle) * 0.2);
        }
        if (random.nextInt(3) == 0)
            level.addParticle(ParticleTypes.FALLING_WATER, x + (random.nextDouble() - 0.5) * 0.5, y, z + (random.nextDouble() - 0.5) * 0.5, 0.0, 0.0, 0.0);
    }
}
