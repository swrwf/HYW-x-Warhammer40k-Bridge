package com.hyww40k.mixin;

import com.hyww40k.Faction;
import com.hyww40k.SoldierOrderManager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ydmsama.hundred_years_war.main.entity.entities.BaseCombatEntity;

import java.util.Set;

/**
 * 让 HYW 士兵把「已选敌对阵营」的 WH40k 生物判定为有效目标（isValidTarget=true），
 * 从而走 HYW 正常索敌/攻击流程——这样士兵会同时攻击 WH40k 阵营生物和原版/其他 mod 的敌对生物，
 * 不再用 setForceAttackTarget 强制只打 WH40k 目标。
 */
@Mixin(BaseCombatEntity.class)
public abstract class BaseCombatEntityTargetMixin {
    @Inject(method = "isValidTarget", at = @At("HEAD"), cancellable = true)
    private void hyw$isValidTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target == null) {
            return;
        }
        BaseCombatEntity self = (BaseCombatEntity) (Object) this;
        if (self.getCurrentCommandedGoal() != null) {
            return; // 有 HYW 原版指令，优先执行，不干预索敌
        }
        Set<Faction> order = SoldierOrderManager.getOrder(self.getUUID());
        if (order == null || order.isEmpty()) {
            return;
        }
        Faction f = Faction.classify(target);
        if (f != null && order.contains(f) && target.isAlive()) {
            cir.setReturnValue(true);
        }
    }
}
