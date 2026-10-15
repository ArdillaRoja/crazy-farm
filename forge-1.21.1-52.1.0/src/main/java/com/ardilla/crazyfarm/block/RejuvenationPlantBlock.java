package com.ardilla.crazyfarm.block;

import com.ardilla.crazyfarm.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Planta del Fruto del Rejuvenecimiento: cuelga del techo y crece en 4 etapas (age 0 a 3).
 * Solo crece con poca luz. Al madurar se cosecha con clic derecho y vuelve a la etapa 1,
 * así produce frutos de forma continua. Romperla no devuelve el brote.
 */
public class RejuvenationPlantBlock extends Block
{
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    /** Luz máxima (0 a 15) con la que la planta crece. Valor inicial, se ajusta probando. */
    public static final int MAX_GROW_LIGHT = 7;
    /** Cada tirada aleatoria avanza una etapa con probabilidad 1 entre este número. */
    public static final int GROW_ODDS = 5;

    private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);

    public RejuvenationPlantBlock(Properties properties)
    {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    /** Necesita un bloque firme encima. */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return Block.canSupportCenter(level, pos.above(), Direction.DOWN);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if (direction == Direction.UP && !state.canSurvive(level, pos))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        int age = state.getValue(AGE);
        if (age < MAX_AGE && level.getRawBrightness(pos, 0) <= MAX_GROW_LIGHT && random.nextInt(GROW_ODDS) == 0)
            level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
    }

    /** Cosecha: madura da 1 fruto (a veces 2) y vuelve a la etapa 1. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (state.getValue(AGE) < MAX_AGE)
            return InteractionResult.PASS;
        if (!level.isClientSide)
        {
            int count = 1 + (level.random.nextInt(3) == 0 ? 1 : 0);
            Block.popResource(level, pos, new ItemStack(ModItems.REJUVENATION_FRUIT.get(), count));
            level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0f, 0.8f + level.random.nextFloat() * 0.4f);
            level.setBlock(pos, state.setValue(AGE, 1), Block.UPDATE_CLIENTS);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Clic central en creativo da el brote. */
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state)
    {
        return new ItemStack(ModItems.REJUVENATION_SPROUT.get());
    }
}
