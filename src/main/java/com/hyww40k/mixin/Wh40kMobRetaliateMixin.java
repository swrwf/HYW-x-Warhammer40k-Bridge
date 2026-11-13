package com.hyww40k.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让战锤40k 生物（warhammer40kmod 命名空间）被攻击时还手：
 * 受伤成功后把自身目标设为攻击者，从而反击 HYW 士兵、玩家等攻击者。
 */
@Mixin(LivingEntity.class)
public abstract class Wh40kMobRetaliateMixin {
    @Inject(method = "hurt", at = @At("RETURN"))
    private void hyw$hurtRetaliate(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return; // 受伤失败（无敌/被挡）则不还手
        }
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Mob mob) || self.level().isClientSide()) {
            return;
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        if (id == null || !"warhammer40kmod".equals(id.getNamespace())) {
            return;
        }
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity living && attacker != mob && living.isAlive()) {
            mob.setTarget(living);
        }
    }
}
