package com.hyww40k.network;

import com.hyww40k.Faction;
import com.hyww40k.SoldierOrderManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import ydmsama.hundred_years_war.main.entity.entities.BaseCombatEntity;
import ydmsama.hundred_years_war.main.utils.ServerRelationHelper;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

/** 客户端命令：设置/取消玩家所有士兵的敌对阵营（可多选）。 */
public class C2SAttackCommand {
    private final String[] factions;

    public C2SAttackCommand(String[] factions) {
        this.factions = factions;
    }

    public C2SAttackCommand(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        this.factions = new String[n];
        for (int i = 0; i < n; i++) {
            this.factions[i] = buf.readUtf();
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(factions.length);
        for (String f : factions) {
            buf.writeUtf(f);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            Set<Faction> set = EnumSet.noneOf(Faction.class);
            for (String name : factions) {
                Faction f = Faction.byName(name);
                if (f != null) {
                    set.add(f);
                }
            }
            // 只记录敌对阵营；实际索敌/攻击由 BaseCombatEntityTargetMixin 让士兵正常 AI 完成
            AABB box = player.getBoundingBox().inflate(200.0);
            for (BaseCombatEntity soldier : player.level().getEntitiesOfClass(BaseCombatEntity.class, box,
                    s -> ServerRelationHelper.hasControlOver(player, s))) {
                if (!soldier.isAlive()) {
                    continue;
                }
                if (set.isEmpty()) {
                    // 取消敌对
                    SoldierOrderManager.clearOrder(soldier.getUUID());
                    soldier.setHywTarget(null);
                } else {
                    SoldierOrderManager.setOrder(soldier, set);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
