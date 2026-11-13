package com.hyww40k;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;

/**
 * 通过实体注册名（warhammer40kmod:xxx）把战锤40k 生物归类到阵营。
 */
public enum Faction {
    CHAOS("混沌",
            "chaos", "dark_legion", "cultist", "renegade", "daemonette", "blood_letter", "blood_crusher",
            "vindicator", "defiler", "tzeench", "nurgle", "nurgling", "slaanesh", "khorne",
            "villager_demon", "furry_cultist", "wolrd_bearer", "noise_marine", "traitor",
            "saqueador", "abaddon", "abbadon"),
    NECRON("太空死灵",
            "necron", "ctan", "flayed", "trazyn", "silent_king", "pariah"),
    TYRANID("泰伦虫族",
            "tyranid", "genstealer", "hormagaunt", "termagaunt", "ripper", "carnifex",
            "zoanthrope", "tyrant", "parasite_worm", "hive"),
    ORK("兽人",
            "ork", "gretchin", "squig", "squiggoth", "meganobz", "killacan", "nob",
            "shoota", "wartruk", "biguns", "waaagh"),
    TAU("钛族",
            "tau", "kroot", "knarloc", "kroothound", "vespid", "guevesa"),
    ELDAR("灵族",
            "eldar", "drukhari", "mandrake", "harlequin", "farseer", "banshee"),
    IMPERIUM("人类帝国",
            "ultra_marine", "ultramarine", "blood_raven", "guardsmen", "commissar", "catachan",
            "krieg", "karskin", "vostroyan", "artic_guard", "ogryn", "mechanicus", "sister",
            "custode", "ciaphas", "space_marine", "astartes", "inquisition", "imperial");

    private final String displayName;
    private final String[] keywords;

    Faction(String displayName, String... keywords) {
        this.displayName = displayName;
        this.keywords = keywords;
    }

    public String displayName() {
        return displayName;
    }

    /** 返回该生物所属阵营；若不是战锤40k 生物返回 null。 */
    public static Faction classify(LivingEntity entity) {
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (key == null || !"warhammer40kmod".equals(key.getNamespace())) {
            return null;
        }
        String path = key.getPath();
        for (Faction f : values()) {
            for (String kw : f.keywords) {
                if (path.contains(kw)) {
                    return f;
                }
            }
        }
        return null;
    }

    public static Faction byName(String name) {
        for (Faction f : values()) {
            if (f.name().equals(name)) {
                return f;
            }
        }
        return null;
    }
}
