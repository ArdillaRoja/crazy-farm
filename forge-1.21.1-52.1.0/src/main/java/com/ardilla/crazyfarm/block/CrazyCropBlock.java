package com.ardilla.crazyfarm.block;

import com.ardilla.crazyfarm.farming.CropYield;
import com.ardilla.crazyfarm.farming.Irrigation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Cultivo base de Crazy Farm: 4 etapas (age 0 a 3), 1 a 4 frutos al cosechar, bonemeal más lento
 * y una probabilidad de mutar a su versión gigante al madurar cerca de un dispositivo de riego.
 */
public class CrazyCropBlock extends CropBlock
{
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    private final Supplier<? extends ItemLike> seed;
    private final Supplier<? extends ItemLike> produce;
    private final Supplier<? extends Block> mutant;

    public CrazyCropBlock(Properties properties, Supplier<? extends ItemLike> seed,
                          Supplier<? extends ItemLike> produce, Supplier<? extends Block> mutant)
    {
        super(properties);
        this.seed = seed;
        this.produce = produce;
        this.mutant = mutant;
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
        return seed.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AGE);
    }

    /** Vanilla avanza de 2 a 5 etapas por bonemeal; aquí 1 etapa, y 2 con un 25% de suerte. */
    @Override
    protected int getBonemealAgeIncrease(Level level)
    {
        return level.random.nextFloat() < 0.25f ? 2 : 1;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        super.randomTick(state, level, pos, random);
        tryMutate(state, level, pos, random);
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state)
    {
        super.growCrops(level, pos, state);
        if (level instanceof ServerLevel serverLevel)
            tryMutate(state, serverLevel, pos, serverLevel.random);
    }

    /** Si el cultivo acaba de madurar, puede convertirse en su versión gigante según el riego. */
    private void tryMutate(BlockState before, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (isMaxAge(before))
            return;
        BlockState after = level.getBlockState(pos);
        if (!after.is(this) || !isMaxAge(after))
            return;
        double chance = Irrigation.mutationChance(Irrigation.levelAt(level, pos));
        if (chance > 0.0 && random.nextDouble() < chance)
            level.setBlock(pos, mutant.get().defaultBlockState(), Block.UPDATE_ALL);
    }

    /** El loot table da el primer fruto y las semillas; aquí se suman los frutos extra. */
    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params)
    {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, params));
        Vec3 origin = params.getOptionalParameter(LootContextParams.ORIGIN);
        if (isMaxAge(state) && origin != null)
        {
            ServerLevel level = params.getLevel();
            int extra = CropYield.rollExtra(level.random, Irrigation.levelAt(level, BlockPos.containing(origin)));
            if (extra > 0)
                drops.add(new ItemStack(produce.get(), extra));
        }
        return drops;
    }
}
