package com.hyww40k;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import virtuoel.pehkui.api.ScaleOperations;
import virtuoel.pehkui.api.ScaleTypes;

/**
 * 复刻 Warhammer40k mod 的四套护甲套装加成（Wear*ArmorProcedure）：
 * 玩家穿整套时获得药水效果 + Pehkui 体型缩放，这里让士兵也能获得同样加成。
 * 护甲识别完全按 Warhammer40k mod 的物品标签（forge:...）。
 */
public final class ArmorSets {
    private ArmorSets() {}

    private static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, new ResourceLocation("forge", path));
    }

    private static boolean fullSet(LivingEntity e, String head, String chest, String legs, String feet) {
        return e.getItemBySlot(EquipmentSlot.HEAD).is(tag(head))
                && e.getItemBySlot(EquipmentSlot.CHEST).is(tag(chest))
                && e.getItemBySlot(EquipmentSlot.LEGS).is(tag(legs))
                && e.getItemBySlot(EquipmentSlot.FEET).is(tag(feet));
    }

    private static void effect(LivingEntity e, MobEffect effect, int duration, int amplifier) {
        e.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }

    private static void scale(LivingEntity e, double height, double width, double reach) {
        ScaleTypes.HEIGHT.getScaleData(e).setTargetScale(
                (float) ScaleOperations.SET.applyAsDouble(ScaleTypes.HEIGHT.getScaleData(e).getTargetScale(), height));
        ScaleTypes.WIDTH.getScaleData(e).setTargetScale(
                (float) ScaleOperations.SET.applyAsDouble(ScaleTypes.WIDTH.getScaleData(e).getTargetScale(), width));
        if (reach > 0) {
            ScaleTypes.REACH.getScaleData(e).setTargetScale(
                    (float) ScaleOperations.SET.applyAsDouble(ScaleTypes.REACH.getScaleData(e).getTargetScale(), reach));
        }
    }

    public static void apply(LivingEntity e) {
        if (e.level().isClientSide()) {
            return;
        }

        // Primaris 原铸（4 个不同标签）
        if (fullSet(e, "nbt_primaris_helmet", "nbt_primaris_chestplate", "nbt_primaris_leggings", "nbt_primaris_boots")) {
            effect(e, MobEffects.DAMAGE_RESISTANCE, 200, 0);
            effect(e, MobEffects.DAMAGE_BOOST, 200, 0);
            effect(e, MobEffects.HEALTH_BOOST, 200, 0);
            effect(e, MobEffects.MOVEMENT_SPEED, 200, 0);
            scale(e, 1.4, 1.27, 1.25);
            return;
        }

        // Gravis 重装（4 个不同标签）
        if (fullSet(e, "nbt_gravis_helmet", "nbt_gravis_chestplate", "nbt_gravis_leggings", "nbt_gravis_boots")) {
            effect(e, MobEffects.DAMAGE_RESISTANCE, 200, 0);
            effect(e, MobEffects.DAMAGE_BOOST, 200, 1);
            effect(e, MobEffects.HEALTH_BOOST, 200, 1);
            scale(e, 1.45, 1.36, -1);
            return;
        }

        // Terminator 终结者（四格同一标签）
        if (fullSet(e, "indomitus_terminator_armor", "indomitus_terminator_armor", "indomitus_terminator_armor", "indomitus_terminator_armor")) {
            effect(e, MobEffects.DAMAGE_RESISTANCE, 200, 1);
            effect(e, MobEffects.DAMAGE_BOOST, 200, 2);
            effect(e, MobEffects.MOVEMENT_SLOWDOWN, 200, 0);
            effect(e, MobEffects.HEALTH_BOOST, 200, 3);
            scale(e, 1.53, 1.48, 1.3);
            return;
        }

        // Tyberos（四格同一标签）
        if (fullSet(e, "tyberos_armor", "tyberos_armor", "tyberos_armor", "tyberos_armor")) {
            effect(e, MobEffects.DAMAGE_RESISTANCE, 200, 1);
            effect(e, MobEffects.DAMAGE_BOOST, 200, 2);
            effect(e, MobEffects.MOVEMENT_SLOWDOWN, 200, 0);
            effect(e, MobEffects.HEALTH_BOOST, 200, 3);
            scale(e, 1.64, 1.6, 1.35);
        }
    }
}
