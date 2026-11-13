package com.hyww40k.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ydmsama.hundred_years_war.main.utils.WeaponTypeHelper;

/**
 * 让 HYW 的武器/盾牌识别也能按标签识别 Warhammer40k 的物品。
 * 原 WeaponTypeHelper 对剑/斧看 minecraft:swords / minecraft:axes 标签（已通过 datapack 加入），
 * 但对盾牌只看 `instanceof ShieldItem`（类判断），所以这里额外注入按标签判断。
 */
@Mixin(WeaponTypeHelper.class)
public abstract class WeaponTypeHelperMixin {
    private static final TagKey<Item> SWORDS = TagKey.create(Registries.ITEM, new ResourceLocation("hyw_w40k_bridge", "swords"));
    private static final TagKey<Item> AXES = TagKey.create(Registries.ITEM, new ResourceLocation("hyw_w40k_bridge", "axes"));
    private static final TagKey<Item> SHIELDS = TagKey.create(Registries.ITEM, new ResourceLocation("hyw_w40k_bridge", "shields"));

    @Inject(method = "isSwordLikeWeapon", at = @At("HEAD"), cancellable = true)
    private static void hyw$isSwordLikeWeapon(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && !stack.isEmpty() && stack.is(SWORDS)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isAxeLikeWeapon", at = @At("HEAD"), cancellable = true)
    private static void hyw$isAxeLikeWeapon(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && !stack.isEmpty() && stack.is(AXES)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isShieldLikeItem", at = @At("HEAD"), cancellable = true)
    private static void hyw$isShieldLikeItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && !stack.isEmpty() && stack.is(SHIELDS)) {
            cir.setReturnValue(true);
        }
    }
}
