package com.ardilla.crazyfarm.registry;

import com.ardilla.crazyfarm.CrazyFarm;
import com.ardilla.crazyfarm.block.CrazyCropBlock;
import com.ardilla.crazyfarm.block.IrrigationDeviceBlock;
import com.ardilla.crazyfarm.block.MutantCropBlock;
import com.ardilla.crazyfarm.block.RejuvenationPlantBlock;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CrazyFarm.MODID);

    // Cultivos mutantes (se declaran antes que los cultivos, que los referencian)
    public static final RegistryObject<Block> ONION_GIANT_CROP = mutant("onion_giant_crop", ModItems.GIANT_ONION);
    public static final RegistryObject<Block> TURNIP_GIANT_CROP = mutant("turnip_giant_crop", ModItems.GIANT_TURNIP);
    public static final RegistryObject<Block> RADISH_GIANT_CROP = mutant("radish_giant_crop", ModItems.GIANT_RADISH);
    public static final RegistryObject<Block> CASSAVA_GIANT_CROP = mutant("cassava_giant_crop", ModItems.GIANT_CASSAVA);

    // Cultivos
    public static final RegistryObject<Block> ONION_CROP = crop("onion_crop", ModItems.ONION_SEEDS, ModItems.ONION, ONION_GIANT_CROP);
    public static final RegistryObject<Block> TURNIP_CROP = crop("turnip_crop", ModItems.TURNIP_SEEDS, ModItems.TURNIP, TURNIP_GIANT_CROP);
    public static final RegistryObject<Block> RADISH_CROP = crop("radish_crop", ModItems.RADISH_SEEDS, ModItems.RADISH, RADISH_GIANT_CROP);
    public static final RegistryObject<Block> CASSAVA_CROP = crop("cassava_crop", ModItems.CASSAVA_CUTTING, ModItems.CASSAVA, CASSAVA_GIANT_CROP);

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

    // Dispositivos de riego, niveles 1 a 4
    public static final RegistryObject<Block> IRRIGATION_DEVICE_1 = device("irrigation_device_1", 1);
    public static final RegistryObject<Block> IRRIGATION_DEVICE_2 = device("irrigation_device_2", 2);
    public static final RegistryObject<Block> IRRIGATION_DEVICE_3 = device("irrigation_device_3", 3);
    public static final RegistryObject<Block> IRRIGATION_DEVICE_4 = device("irrigation_device_4", 4);

    // Mineral de Vitilium: tan duro como la obsidiana y solo se pica con pico de diamante o mejor
    public static final RegistryObject<Block> VITILIUM_ORE = ore("vitilium_ore", MapColor.STONE, SoundType.STONE);
    public static final RegistryObject<Block> DEEPSLATE_VITILIUM_ORE = ore("deepslate_vitilium_ore", MapColor.DEEPSLATE, SoundType.DEEPSLATE);

    private static RegistryObject<Block> ore(String name, MapColor color, SoundType sound)
    {
        return BLOCKS.register(name, () -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(50.0f, 1200.0f)
                .sound(sound)));
    }

    private static RegistryObject<Block> crop(String name, Supplier<? extends Item> seed, Supplier<? extends Item> produce,
                                              Supplier<? extends Block> mutant)
    {
        return BLOCKS.register(name, () -> new CrazyCropBlock(cropProperties(), seed, produce, mutant));
    }

    private static RegistryObject<Block> mutant(String name, Supplier<? extends Item> giantItem)
    {
        return BLOCKS.register(name, () -> new MutantCropBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY), giantItem));
    }

    private static RegistryObject<Block> device(String name, int tier)
    {
        return BLOCKS.register(name, () -> new IrrigationDeviceBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0f)
                .sound(SoundType.METAL)
                .noOcclusion(), tier));
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
