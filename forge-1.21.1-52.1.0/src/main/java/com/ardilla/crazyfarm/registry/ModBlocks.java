package com.ardilla.crazyfarm.registry;

import com.ardilla.crazyfarm.CrazyFarm;
import com.ardilla.crazyfarm.block.CrazyCropBlock;
import com.ardilla.crazyfarm.block.RejuvenationPlantBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CrazyFarm.MODID);

    public static final RegistryObject<Block> ONION_CROP = crop("onion_crop", ModItems.ONION_SEEDS, ModItems.ONION);
    public static final RegistryObject<Block> TURNIP_CROP = crop("turnip_crop", ModItems.TURNIP_SEEDS, ModItems.TURNIP);
    public static final RegistryObject<Block> RADISH_CROP = crop("radish_crop", ModItems.RADISH_SEEDS, ModItems.RADISH);
    public static final RegistryObject<Block> CASSAVA_CROP = crop("cassava_crop", ModItems.CASSAVA_CUTTING, ModItems.CASSAVA);

    public static final RegistryObject<Block> REJUVENATION_PLANT = BLOCKS.register("rejuvenation_plant",
            () -> new RejuvenationPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CAVE_VINES)
                    // Luz tenue solo con el fruto: etapa 3 = 5, etapa 2 = 2. Debe ser menor que MAX_GROW_LIGHT.
                    .lightLevel(state -> switch (state.getValue(RejuvenationPlantBlock.AGE)) { case 3 -> 5; case 2 -> 2; default -> 0; })
                    .pushReaction(PushReaction.DESTROY)));

    private static RegistryObject<Block> crop(String name, Supplier<? extends Item> seed, Supplier<? extends Item> produce)
    {
        return BLOCKS.register(name, () -> new CrazyCropBlock(cropProperties(), seed, produce));
    }

    private static BlockBehaviour.Properties cropProperties()
    {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY);
    }

    private ModBlocks() {}
}
