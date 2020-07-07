package com.github.sejoslaw.crystalofimperceptibility;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

@Mod(CrystalOfImperceptibility.MODID)
public class CrystalOfImperceptibility {
    public static final String MODID = "crystalofimperceptibility";

    public CrystalOfImperceptibility() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void tickInvisibility(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;

        if (!player.inventory.hasItemStack(new ItemStack(Items.NETHER_STAR))) {
            return;
        }

        World world = player.world;
        BlockPos playerPos = player.getPosition();
        int scannedDistance = 100; // Number of blocks

        AxisAlignedBB scannedBox = new AxisAlignedBB(
                playerPos.getX() - scannedDistance,
                playerPos.getY() - scannedDistance,
                playerPos.getZ() - scannedDistance,
                playerPos.getX() + scannedDistance,
                playerPos.getY() + scannedDistance,
                playerPos.getZ() + scannedDistance);

        List<MobEntity> mobsNearbyPlayer = world.getEntitiesWithinAABB(MobEntity.class, scannedBox);

        mobsNearbyPlayer.forEach(mob -> {
            try {
                Field goalsField = GoalSelector.class.getDeclaredField("goals");
                goalsField.setAccessible(true);

                Set<PrioritizedGoal> goals = (Set<PrioritizedGoal>) goalsField.get(mob.targetSelector);

                if (!goals.isEmpty()) {
                    goals.clear();
                }

                if (mob.getAttackTarget() != null) {
                    mob.setAttackTarget(null);
                }

                if (mob.getRevengeTarget() != null) {
                    mob.setRevengeTarget(null);
                }

                if (mob.isAggressive()) {
                    mob.setAggroed(false);
                }

                if (mob.getLastAttackedEntity() != null) {
                    mob.setLastAttackedEntity(null);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        });
    }
}
