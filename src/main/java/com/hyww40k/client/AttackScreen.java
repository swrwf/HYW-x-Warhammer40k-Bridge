package com.hyww40k.client;

import com.hyww40k.Faction;
import com.hyww40k.network.C2SAttackCommand;
import com.hyww40k.network.Network;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * 攻击选择栏：右侧多选目标阵营，点「命令攻击」让玩家所有自己的士兵统一听命。
 */
@OnlyIn(Dist.CLIENT)
public class AttackScreen extends Screen {
    private final Set<Faction> selected = EnumSet.noneOf(Faction.class);
    private final List<Button> factionButtons = new ArrayList<>();

    public AttackScreen() {
        super(Component.translatable("screen.hyww40k.attack"));
    }

    @Override
    protected void init() {
        int fx = this.width - 170;
        int fy = 40;
        for (Faction f : Faction.values()) {
            final Faction ff = f;
            Button b = Button.builder(Component.literal(f.displayName()), btn -> toggleFaction(ff))
                    .bounds(fx, fy, 150, 20).build();
            addRenderableWidget(b);
            factionButtons.add(b);
            fy += 22;
        }

        addRenderableWidget(Button.builder(Component.translatable("screen.hyww40k.attack_all"), btn -> doAttack())
                .bounds(fx, this.height - 40, 150, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.hyww40k.cancel_all"), btn -> doCancel())
                .bounds(fx, this.height - 62, 150, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.hyww40k.select_all"), btn -> {
                    selected.addAll(EnumSet.allOf(Faction.class));
                    rebuildFactions();
                })
                .bounds(fx, this.height - 86, 73, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.hyww40k.deselect_all"), btn -> {
                    selected.clear();
                    rebuildFactions();
                })
                .bounds(fx + 77, this.height - 86, 73, 20).build());

        rebuildFactions();
    }

    private void toggleFaction(Faction f) {
        if (selected.contains(f)) {
            selected.remove(f);
        } else {
            selected.add(f);
        }
        rebuildFactions();
    }

    private void rebuildFactions() {
        int i = 0;
        for (Faction f : Faction.values()) {
            factionButtons.get(i).setMessage(Component.literal((selected.contains(f) ? "[✓] " : "[  ] ") + f.displayName()));
            i++;
        }
    }

    private void doAttack() {
        if (selected.isEmpty()) {
            return;
        }
        String[] arr = selected.stream().map(Enum::name).toArray(String[]::new);
        Network.CHANNEL.sendToServer(new C2SAttackCommand(arr));
        onClose();
    }

    private void doCancel() {
        // 发送空阵营列表 = 取消敌对，士兵不再攻击该阵营生物
        Network.CHANNEL.sendToServer(new C2SAttackCommand(new String[0]));
        onClose();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        g.drawString(this.font, Component.translatable("screen.hyww40k.faction_multi"), this.width - 170, 20, 0xFFFFFF, false);
        g.drawString(this.font, Component.translatable("screen.hyww40k.unified"), 10, 20, 0xFFAA00, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
