package com.hyww40k;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器端保存「士兵 -> 目标阵营集合」的命令，供 tick 时持续索敌。
 */
public final class SoldierOrderManager {
    private static final Map<UUID, Set<Faction>> ORDERS = new ConcurrentHashMap<>();

    private SoldierOrderManager() {}

    public static void setOrder(Entity soldier, Set<Faction> factions) {
        if (factions == null || factions.isEmpty()) {
            ORDERS.remove(soldier.getUUID());
        } else {
            // EnumSet 对 contains(null) 返回 false，避免非 WH40k 生物导致 NPE
            ORDERS.put(soldier.getUUID(), EnumSet.copyOf(factions));
        }
    }

    public static Set<Faction> getOrder(UUID uuid) {
        return ORDERS.get(uuid);
    }

    public static void clearOrder(UUID uuid) {
        ORDERS.remove(uuid);
    }

    /** 在士兵的索敌距离（FOLLOW_RANGE，与原版敌对生物一致）内找最近的、属于任一指定阵营的生物。 */
    public static LivingEntity findNearestFactionMob(LivingEntity soldier, Set<Faction> factions) {
        if (!(soldier.level() instanceof ServerLevel level)) {
            return null;
        }
        double range = soldier.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB box = soldier.getBoundingBox().inflate(range);
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> {
            if (t == soldier || !t.isAlive()) {
                return false;
            }
            Faction f = Faction.classify(t);
            return f != null && factions.contains(f);
        })) {
            double d = soldier.distanceToSqr(e);
            if (d < bestDist) {
                bestDist = d;
                best = (LivingEntity) e;
            }
        }
        return best;
    }
}
