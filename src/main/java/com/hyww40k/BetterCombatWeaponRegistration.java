package com.hyww40k;

import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 把 Warhammer40k 的近战武器注册到 Better Combat 的 WeaponRegistry，
 * 这样 (1) 玩家持有时显示 bettercombat 的挥舞模型，(2) HYW 士兵持有时
 * 能像 HYW×bettercombat 正常联动一样挥舞（BetterCombatCompatBridge 通过
 * WeaponRegistry.getAttributes 判定武器）。
 */
@Mod.EventBusSubscriber(modid = "hyw_w40k_bridge", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BetterCombatWeaponRegistration {
    private static final String NS = "warhammer40kmod";

    private static final String SWORD = """
            {"attributes":{"attack_range":2.5,"category":"sword","attacks":[
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":1,"angle":120,"upswing":0.5,"animation":"bettercombat:one_handed_slash_horizontal_right","swing_sound":{"id":"bettercombat:sword_slash"}},
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":1,"angle":120,"upswing":0.5,"animation":"bettercombat:one_handed_slash_horizontal_left","swing_sound":{"id":"bettercombat:sword_slash"}},
              {"hitbox":"FORWARD_BOX","damage_multiplier":1,"angle":0,"upswing":0.5,"animation":"bettercombat:one_handed_stab","swing_sound":{"id":"bettercombat:sword_slash","pitch":1.2}}
            ]}}""";

    private static final String AXE = """
            {"attributes":{"attack_range":2.5,"category":"axe","attacks":[
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":1,"angle":90,"upswing":0.5,"animation":"bettercombat:one_handed_slash_horizontal_right","swing_sound":{"id":"bettercombat:axe_slash"}},
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":1,"angle":90,"upswing":0.5,"animation":"bettercombat:one_handed_slash_horizontal_left","swing_sound":{"id":"bettercombat:axe_slash"}}
            ]}}""";

    private static final String HAMMER = """
            {"attributes":{"attack_range":2.75,"pose":"bettercombat:pose_two_handed_heavy","two_handed":true,"category":"hammer","attacks":[
              {"hitbox":"FORWARD_BOX","damage_multiplier":1,"angle":0,"upswing":0.5,"animation":"bettercombat:two_handed_slam","swing_sound":{"id":"bettercombat:hammer_slam"}}
            ]}}""";

    private static final String DAGGER = """
            {"attributes":{"attack_range":2,"two_handed":false,"category":"dagger","attacks":[
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":0.8,"angle":150,"upswing":0.5,"animation":"bettercombat:one_handed_slash_horizontal_right","swing_sound":{"id":"bettercombat:dagger_slash"}},
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":0.8,"angle":150,"upswing":0.5,"animation":"bettercombat:one_handed_slash_horizontal_left","swing_sound":{"id":"bettercombat:dagger_slash"}}
            ]}}""";

    private static final String SPEAR = """
            {"attributes":{"attack_range":3.5,"pose":"bettercombat:pose_two_handed_polearm","two_handed":true,"category":"spear","attacks":[
              {"hitbox":"FORWARD_BOX","damage_multiplier":1,"angle":0,"upswing":0.5,"animation":"bettercombat:two_handed_stab_right","swing_sound":{"id":"bettercombat:spear_stab"}}
            ]}}""";

    private static final String STAFF = """
            {"attributes":{"attack_range":3,"two_handed":true,"category":"staff","attacks":[
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":0.8,"angle":160,"upswing":0.5,"animation":"bettercombat:two_handed_slash_horizontal_right","swing_sound":{"id":"bettercombat:staff_slash"}},
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":1,"angle":160,"upswing":0.5,"animation":"bettercombat:two_handed_slash_horizontal_left","swing_sound":{"id":"bettercombat:staff_slash"}}
            ]}}""";

    private static final String SCYTHE = """
            {"attributes":{"attack_range":3.5,"pose":"bettercombat:pose_two_handed_scythe","two_handed":true,"category":"scythe","attacks":[
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":1,"angle":150,"upswing":0.5,"animation":"bettercombat:two_handed_slash_horizontal_right","swing_sound":{"id":"bettercombat:scythe_slash"}},
              {"hitbox":"HORIZONTAL_PLANE","damage_multiplier":1,"angle":150,"upswing":0.5,"animation":"bettercombat:two_handed_slash_horizontal_left","swing_sound":{"id":"bettercombat:scythe_slash"}}
            ]}}""";

    private static final String CLAW = """
            {"attributes":{"attack_range":2.1,"category":"claw","attacks":[
              {"hitbox":"FORWARD_BOX","upswing":0.5,"animation":"bettercombat:one_handed_punch","swing_sound":{"id":"bettercombat:fist_punch"}},
              {"hitbox":"HORIZONTAL_PLANE","angle":120,"upswing":0.5,"animation":"bettercombat:one_handed_swipe_horizontal_right","swing_sound":{"id":"bettercombat:sword_slash"}}
            ]}}""";

    private static final Map<String, WeaponAttributes> TEMPLATES = new LinkedHashMap<>();

    private BetterCombatWeaponRegistration() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (!net.minecraftforge.fml.ModList.get().isLoaded("bettercombat")) {
            return;
        }
        if (TEMPLATES.isEmpty()) {
            loadTemplate("sword", SWORD);
            loadTemplate("axe", AXE);
            loadTemplate("hammer", HAMMER);
            loadTemplate("dagger", DAGGER);
            loadTemplate("spear", SPEAR);
            loadTemplate("staff", STAFF);
            loadTemplate("scythe", SCYTHE);
            loadTemplate("claw", CLAW);
        }
        int registered = 0;
        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (!NS.equals(id.getNamespace())) {
                continue;
            }
            WeaponAttributes attrs = categorize(id.getPath());
            if (attrs == null) {
                continue;
            }
            WeaponRegistry.register(id, attrs);
            registered++;
        }
        if (registered > 0) {
            System.out.println("[hyw_w40k_bridge] Registered " + registered + " Warhammer40k melee weapons to Better Combat");
        }
    }

    private static void loadTemplate(String key, String json) {
        try {
            AttributesContainer container = WeaponAttributesHelper.decode(new StringReader(json));
            WeaponAttributes attrs = container.attributes();
            if (attrs != null) {
                TEMPLATES.put(key, attrs);
            }
        } catch (Exception e) {
            System.err.println("[hyw_w40k_bridge] Failed to decode weapon template " + key + ": " + e.getMessage());
        }
    }

    private static WeaponAttributes categorize(String path) {
        if (path.contains("_sword") || path.contains("sword") || path.contains("_blade") || path.contains("saber") || path.contains("rapier")) {
            return TEMPLATES.get("sword");
        }
        if (path.contains("_axe") || path.contains("axe") || path.contains("choppa")) {
            return TEMPLATES.get("axe");
        }
        if (path.contains("hammer") || path.contains("_maul") || path.contains("maul")) {
            return TEMPLATES.get("hammer");
        }
        if (path.contains("_knife") || path.contains("knife") || path.contains("_dagger")) {
            return TEMPLATES.get("dagger");
        }
        if (path.contains("_spear") || path.contains("spear")) {
            return TEMPLATES.get("spear");
        }
        if (path.contains("_staff") || path.contains("staff")) {
            return TEMPLATES.get("staff");
        }
        if (path.contains("_scythe") || path.contains("scythe")) {
            return TEMPLATES.get("scythe");
        }
        if (path.contains("_claw") || path.contains("claw") || path.contains("_fist") || path.contains("fist")) {
            return TEMPLATES.get("claw");
        }
        return null;
    }
}
