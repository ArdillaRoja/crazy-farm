package com.ardilla.crazyfarm.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

/** Añade un ítem (con probabilidad y cantidad) al loot de las tablas que cumplan las condiciones. */
public class AddItemLootModifier extends LootModifier
{
    public static final MapCodec<AddItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
            .and(inst.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item),
                    Codec.floatRange(0f, 1f).fieldOf("chance").forGetter(m -> m.chance),
                    Codec.intRange(1, 64).fieldOf("min").forGetter(m -> m.min),
                    Codec.intRange(1, 64).fieldOf("max").forGetter(m -> m.max)))
            .apply(inst, AddItemLootModifier::new));

    private final Item item;
    private final float chance;
    private final int min;
    private final int max;

    public AddItemLootModifier(LootItemCondition[] conditions, Item item, float chance, int min, int max)
    {
        super(conditions);
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = Math.max(min, max);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        if (context.getRandom().nextFloat() < chance)
        {
            int count = min + context.getRandom().nextInt(max - min + 1);
            generatedLoot.add(new ItemStack(item, count));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends net.minecraftforge.common.loot.IGlobalLootModifier> codec()
    {
        return CODEC;
    }
}
