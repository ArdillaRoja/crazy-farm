package com.ardilla.crazyfarm.block;

import com.ardilla.crazyfarm.registry.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Cultivo de cebolla: 4 etapas de crecimiento (age 0 a 3). */
public class OnionCropBlock extends CropBlock
{
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public OnionCropBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public IntegerProperty getAgeProperty()
    {
        return AGE;
    }

    @Override
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId()
    {
        return ModItems.ONION_SEEDS.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AGE);
    }
}
