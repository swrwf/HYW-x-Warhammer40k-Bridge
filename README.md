# HYW × Warhammer40k Bridge (hyw_w40k_bridge)

一个让 **百年战争（Hundred Years War / HYW）** 与 **战锤40k（Warhammer 40k）** 联动的 Forge 附属 mod。

A Forge addon mod that bridges **Hundred Years War (HYW)** and **Warhammer 40k**.

---

## 一、这是什么 / What it does

- 让 HYW 士兵穿上 WH40k 整套护甲时，获得与玩家相同的套装加成（抗性/力量/生命提升等）+ Pehkui 体型缩放。
- 攻击选择栏（按 **K** 键）：让玩家命令自己的 HYW 士兵攻击指定 WH40k 阵营（混沌/死灵/泰伦/兽人/钛/灵族/帝国），可多选、可随时取消敌对。
- 让 WH40k 武器使用 bettercombat 的挥舞模型（bettercombat 可选加载）。
- 让 WH40k 武器/盾牌能被 HYW 识别为对应类型（剑/斧/盾牌）。
- 新增 67 个合成表，让 WH40k 里无法获取的套装护甲（原铸 Primaris / 重装 Gravis / 终结者 Terminator / Tyberos）能被合成。
- 让 WH40k 生物被 HYW 士兵攻击时会还手。

---

## 二、运行环境 / Requirements

| 项目 | 版本 |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.x（构建用 47.2.0） |
| Java | 17 |

### 依赖 / Dependencies

| Mod | 必需 | 版本（构建用） |
| --- | --- | --- |
| Hundred Years War（百年战争） | ✅ 必装 | 0.6.4r |
| Warhammer 40k Mod（战锤40k） | ✅ 必装 | 1.0.7 |
| Pehkui | ✅ 必装 | 3.8.2 |
| Better Combat | ⬜ 可选 | 1.9.0 |

---

## 三、如何构建 / Build

1. 安装 **JDK 17**。
2. 使用 **Gradle 8.1.1**（项目自带 `gradlew`）。
3. 把依赖 mod 放进 `libs/`（`hundred_years_war`、`pehkui`、`bettercombat`，均以 `compileOnly` 平铺依赖方式引入）。
4. 构建：

```bash
./gradlew build
```

产物输出到 `build/libs/hyw_w40k_bridge-<version>.jar`。

> 依赖 jar 需提前从 CurseForge / MC百科 / Modrinth 下载，放到 `libs/` 目录，`build.gradle` 里按 `flatDir` 引用。

---

## 四、项目结构 / Project layout

```
src/main/java/com/hyww40k/
├── HywW40kBridge.java              # @Mod 入口，注册网络
├── Faction.java                    # 阵营枚举 + 按实体注册名归类
├── ArmorSets.java                  # 复刻 WH40k 四套护甲套装加成（含 Pehkui 缩放）
├── ArmorSetTickHandler.java        # 每 tick 给士兵应用套装加成
├── SoldierOrderManager.java        # 服务器端保存「士兵→敌对阵营集合」的命令
├── BetterCombatWeaponRegistration.java  # 把 WH40k 武器注册到 bettercombat（可选加载）
├── mixin/
│   ├── WeaponTypeHelperMixin.java        # 让 HYW 按标签识别 WH40k 剑/斧/盾牌
│   ├── BaseCombatEntityTargetMixin.java  # 让士兵把 WH40k 阵营生物当正常索敌目标
│   └── Wh40kMobRetaliateMixin.java       # 让 WH40k 生物被攻击后还手
├── network/
│   ├── Network.java               # SimpleChannel 注册
│   └── C2SAttackCommand.java      # 客户端→服务器：设置/取消敌对阵营
└── client/
    ├── AttackScreen.java          # 攻击选择栏 UI
    ├── Keybinds.java              # 按键绑定（K）
    └── ClientEvents.java          # 打开 UI 的事件
```

### 资源 / Resources

- `hyw_w40k_bridge.mixins.json`（**必须放在 jar 根目录**）
- `data/hyw_w40k_bridge/recipes/` — 67 个护甲合成表
- `data/hyw_w40k_bridge/tags/items/` — 剑/斧/盾牌标签
- `data/{minecraft,simplyswords,c,forge}/tags/items/` — 武器/盾牌类型标签
- `assets/hyw_w40k_bridge/lang/` — en_us.json / zh_cn.json

---

## 五、⚠️ 关键坑（接手必读）/ Important gotchas

### 1. Mixin 配置必须在 jar 根目录，且要在 manifest 里声明

`hyw_w40k_bridge.mixins.json` 必须放在 **jar 根目录**（不是 `META-INF/`）。

并且 `build.gradle` 的 `jar` 任务 manifest 里**必须**有这一行，否则 mixin 会静默不生效：

```gradle
'MixinConfigs': 'hyw_w40k_bridge.mixins.json'
```

### 2. 资源由脚本生成

- `gen_tags.py` → 生成武器/盾牌标签（依赖 WH40k jar）。
- `gen_armor_recipes.py` → 生成 67 个护甲合成表。

改这两类资源后，重新跑对应脚本再构建，不要手改生成文件。

### 3. 合成表设计（1.0.9 起）

- 已排除未完成的「原铸极限战士(Gino)」系列（`ultra_marine_primaris_armor`）。
- 链式升级：基础甲 → 原铸 → 终结者 → Tyberos。
- 材料：原铸=陶钢锭、Gravis=塑钢板、终结者/Tyberos=亚德曼金锭、泰伦陆战队/中士头盔=塑钢锭。
- 每个「原料+材料」组合只对应一个结果（脚本内置冲突校验）。

---

## 六、协议 / License

MIT License，详见 [LICENSE](LICENSE)。

---

## 七、更新日志 / Changelog

见仓库根目录或发布页的 `更新日志.txt`。
