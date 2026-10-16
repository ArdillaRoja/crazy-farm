package com.ardilla.crazyfarm.loot;

import com.ardilla.crazyfarm.ModTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

/**
 * Funde el botín según las recetas de horno. Solo toca los ítems de la etiqueta
 * {@link ModTags.Items#AUTO_SMELT_INPUTS} (minerales crudos), para no convertir, por ejemplo,
 * la piedra labrada en piedra. Las condiciones (qué herramienta) vienen del JSON.
 */
public class SmeltDropsLootModifier extends LootModifier
{
    public static final MapCodec<SmeltDropsLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, SmeltDropsLootModifier::new));

    public SmeltDropsLootModifier(LootItemCondition[] conditions)
    {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        ServerLevel level = context.getLevel();
        ObjectArrayList<ItemStack> result = new ObjectArrayList<>(generatedLoot.size());
        for (ItemStack stack : generatedLoot)
        {
            if (!stack.is(ModTags.Items.AUTO_SMELT_INPUTS))
            {
                result.add(stack);
                continue;
            }
            ItemStack smelted = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level)
                    .map(holder -> holder.value().getResultItem(level.registryAccess()).copy())
                    .orElse(ItemStack.EMPTY);
            if (smelted.isEmpty())
            {
                result.add(stack);
            }
            else
            {
                smelted.setCount(smelted.getCount() * stack.getCount());
                result.add(smelted);
            }
        }
        return result;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return CODEC;
    }
}
