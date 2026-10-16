package com.ardilla.crazyfarm.ability;

import com.ardilla.crazyfarm.CrazyFarm;
import com.ardilla.crazyfarm.ModTags;
import com.ardilla.crazyfarm.item.VitiliumArmorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Habilidades de las herramientas y la armadura de Vitilium. Las de herramientas se activan por
 * etiqueta (ver {@link ModTags.Items}), así que se pueden dar a otros ítems sin tocar código.
 * Valores iniciales, se ajustan probando.
 */
@Mod.EventBusSubscriber(modid = CrazyFarm.MODID)
public final class VitiliumEvents
{
    // --- herramientas ---
    public static final int MAX_TREE_BLOCKS = 128;
    public static final int MAX_TREE_RADIUS = 12;
    public static final float CRIT_CHANCE = 0.20f;
    public static final float CRIT_MULTIPLIER = 1.5f;
    public static final float LIFE_STEAL_FRACTION = 0.10f;

    // --- armadura ---
    /** 5 corazones extra con el set completo. */
    public static final double SET_BONUS_HEALTH = 10.0;
    private static final ResourceLocation SET_BONUS_ID = ResourceLocation.fromNamespaceAndPath(CrazyFarm.MODID, "vitilium_set_bonus");
    private static final int PERMANENT_EFFECT_DURATION = 400;
    /** La visión nocturna parpadea con menos de 200 ticks; se renueva antes de eso. */
    private static final int PERMANENT_EFFECT_RENEW_BELOW = 260;
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    // ---------------------------------------------------------------- herramientas

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if (!(event.getPlayer() instanceof ServerPlayer player) || player.isCrouching())
            return;
        if (!(event.getLevel() instanceof ServerLevel level))
            return;
        ItemStack tool = player.getMainHandItem();
        BlockPos origin = event.getPos();
        BlockState state = event.getState();

        if (tool.is(ModTags.Items.TREE_FELLING) && state.is(BlockTags.LOGS))
            fellTree(level, player, tool, origin);
        else if (tool.is(ModTags.Items.AREA_DIG))
            digArea(level, player, tool, origin);
    }

    private static void fellTree(ServerLevel level, ServerPlayer player, ItemStack tool, BlockPos origin)
    {
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        List<BlockPos> logs = new ArrayList<>();
        seen.add(origin);
        queue.add(origin);
        while (!queue.isEmpty() && logs.size() < MAX_TREE_BLOCKS)
        {
            BlockPos current = queue.poll();
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    for (int dz = -1; dz <= 1; dz++)
                    {
                        BlockPos next = current.offset(dx, dy, dz);
                        if (Math.abs(next.getX() - origin.getX()) > MAX_TREE_RADIUS
                                || Math.abs(next.getZ() - origin.getZ()) > MAX_TREE_RADIUS
                                || !seen.add(next))
                            continue;
                        if (level.getBlockState(next).is(BlockTags.LOGS))
                        {
                            logs.add(next);
                            queue.add(next);
                        }
                    }
        }
        for (BlockPos pos : logs)
        {
            if (tool.isEmpty())
                break;
            level.destroyBlock(pos, true, player);
            tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }

    private static void digArea(ServerLevel level, ServerPlayer player, ItemStack tool, BlockPos origin)
    {
        HitResult hit = player.pick(player.blockInteractionRange() + 1.0, 1.0f, false);
        if (!(hit instanceof BlockHitResult blockHit) || !blockHit.getBlockPos().equals(origin))
            return;
        Direction.Axis axis = blockHit.getDirection().getAxis();
        for (int a = -1; a <= 1; a++)
            for (int b = -1; b <= 1; b++)
            {
                if (a == 0 && b == 0)
                    continue;
                BlockPos pos = switch (axis)
                {
                    case X -> origin.offset(0, a, b);
                    case Y -> origin.offset(a, 0, b);
                    case Z -> origin.offset(a, b, 0);
                };
                if (tool.isEmpty())
                    return;
                BlockState state = level.getBlockState(pos);
                if (!state.isAir() && state.getDestroySpeed(level, pos) >= 0.0f && tool.isCorrectToolForDrops(state))
                {
                    level.destroyBlock(pos, true, player);
                    tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            }
    }

    /** Aran 3x3: las 8 casillas de alrededor se aran antes de que el juego ara la que se tocó. */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack stack = event.getItemStack();
        if (level.isClientSide || player.isCrouching() || event.getFace() != Direction.UP || !stack.is(ModTags.Items.AREA_TILL))
            return;
        BlockPos center = event.getPos();
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
            {
                if (dx == 0 && dz == 0)
                    continue;
                if (stack.isEmpty())
                    return;
                BlockPos pos = center.offset(dx, 0, dz);
                BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos).add(0.0, 0.5, 0.0), Direction.UP, pos, false);
                stack.getItem().useOn(new UseOnContext(level, player, event.getHand(), stack, hit));
            }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event)
    {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getSource().getDirectEntity() != player)
            return;
        ItemStack weapon = player.getMainHandItem();
        LivingEntity target = event.getEntity();
        float amount = event.getAmount();

        if (weapon.is(ModTags.Items.CRITICAL_HIT) && player.getRandom().nextFloat() < CRIT_CHANCE)
        {
            amount *= CRIT_MULTIPLIER;
            event.setAmount(amount);
            ((ServerLevel) player.level()).sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.5), target.getZ(),
                    12, 0.3, 0.3, 0.3, 0.2);
            player.level().playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        if (weapon.is(ModTags.Items.LIFE_STEAL))
            player.heal(amount * LIFE_STEAL_FRACTION);
    }

    // ---------------------------------------------------------------- armadura

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent.Post event)
    {
        Player player = event.player;
        if (player.level().isClientSide || player.tickCount % 20 != 0)
            return;
        updateSetBonus(player);
        for (EquipmentSlot slot : ARMOR_SLOTS)
        {
            if (player.getItemBySlot(slot).getItem() instanceof VitiliumArmorItem armor && armor.getPermanentEffect() != null)
            {
                MobEffectInstance current = player.getEffect(armor.getPermanentEffect());
                if (current == null || current.getDuration() < PERMANENT_EFFECT_RENEW_BELOW)
                    // ambiente, sin partículas y sin ícono: el efecto no se ve ni parpadea
                    player.addEffect(new MobEffectInstance(armor.getPermanentEffect(), PERMANENT_EFFECT_DURATION, 0, true, false, false));
            }
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event)
    {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide)
            return;
        if (event.getFrom().getItem() instanceof VitiliumArmorItem armor && armor.getPermanentEffect() != null
                && !(event.getTo().getItem() == event.getFrom().getItem()))
        {
            MobEffectInstance current = player.getEffect(armor.getPermanentEffect());
            // Solo se quita el efecto oculto de la armadura, no uno que el jugador tenga de una poción
            if (current != null && !current.isVisible())
                player.removeEffect(armor.getPermanentEffect());
        }
        updateSetBonus(player);
    }

    private static void updateSetBonus(Player player)
    {
        boolean fullSet = true;
        for (EquipmentSlot slot : ARMOR_SLOTS)
            fullSet &= player.getItemBySlot(slot).getItem() instanceof VitiliumArmorItem;

        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health == null)
            return;
        if (fullSet && !health.hasModifier(SET_BONUS_ID))
            health.addTransientModifier(new AttributeModifier(SET_BONUS_ID, SET_BONUS_HEALTH, AttributeModifier.Operation.ADD_VALUE));
        else if (!fullSet && health.hasModifier(SET_BONUS_ID))
            health.removeModifier(SET_BONUS_ID);
    }

    private VitiliumEvents() {}
}
