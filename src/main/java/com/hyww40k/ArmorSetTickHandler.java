package com.hyww40k;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ydmsama.hundred_years_war.main.entity.entities.BaseCombatEntity;

/**
 * 监听每个生物的 tick，让百年战争(HYW)的士兵（BaseCombatEntity）在穿戴
 * Warhammer40k 整套护甲时，获得与玩家相同的套装效果与 Pehkui 体型变化。
 */
@Mod.EventBusSubscriber(modid = "hyw_w40k_bridge", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ArmorSetTickHandler {
    private ArmorSetTickHandler() {}

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (entity instanceof BaseCombatEntity) {
            ArmorSets.apply((BaseCombatEntity) entity);
        }
    }
}
