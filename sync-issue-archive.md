# 喝水应用同步问题存档

> 生成日期：2026-06-15
> 涉及：手表版（Oppo Watch 2）、手机版（Web PWA）、Supabase 云服务

---

## 一、架构总览

### 1.1 数据流

```
手机端                      Supabase                        手表端
  │                           │                               │
  ├─ 拉→合→推 ──────→  leaderboard 表 ←─── 拉→合→推 ─────┤
  │                           │                               │
  │                    today_records JSONB                    │
  │                    user_id PK + on_conflict               │
```

### 1.2 两端同步策略（当前稳定版）

| | 手表端 | 手机端 |
|---|---|---|
| 同步顺序 | **拉→合→推** | 拉→合→推 |
| 推送方式 | `POST /rest/v1/leaderboard?on_conflict=user_id` + `Prefer: resolution=merge-duplicates` | 同左 |
| `today_records` 格式 | `[{id, amount, time, mood}]` | 同左 |
| 合并策略 | 按 id 去重追加 | 按 id 去重追加 |

---

## 二、已解决的问题

### 2.1 Supabase upsert 返回 409 但不写入

**症状**：推送日志显示 `HTTP 409`，被当作成功处理，但数据没有真正写入数据库。

**原因**：`POST /rest/v1/leaderboard` 没有指定 `on_conflict` 参数，PostgREST 不知道用哪一列做冲突检测。主键冲突时返回 409，数据不写入。

**修复**：URL 加上 `?on_conflict=user_id`，明确告诉 PostgREST 用 `user_id` 做冲突检测。

```diff
- POST /rest/v1/leaderboard
+ POST /rest/v1/leaderboard?on_conflict=user_id
```

**验证**：修复后返回 `HTTP 200`，数据确认落库。

**教训**：Supabase REST API upsert 必须同时满足两个条件：
1. URL 参数 `?on_conflict=<主键列名>`
2. 请求头 `Prefer: resolution=merge-duplicates`

### 2.2 手表端先推后拉导致覆盖手机数据

**症状**：手表每次同步先用本地数据覆盖云端，手机数据丢失。

**原因**：同步顺序是"先推后拉"，第一步推上去的只有手表本地数据，手机数据被覆盖。

**修复**：改为"先拉后推"——先拉取云端 `today_records`，合并到本地，再推送合并后的全集。

### 2.3 GET 请求间歇性超时

**症状**：手表端拉取云端记录 15 秒超时，时好时坏。

**原因**：Oppo Watch 2 的 WebView（Chrome 61）网络连接不稳定，非代码问题。

**状态**：已恢复，未做代码层面的修复。

---

## 三、当前未解决的问题

### 🔴 P0 — 数据同步

| # | 问题 | 归属 | 根因 | 修复方向 |
|---|------|------|------|---------|
| 1 | **手机端看不到手表推送的数据** | 手机端 | 手机合并逻辑未正确识别手表推送的 `today_records`（可能 time 字段格式或 id 冲突） | 手机端拉取 `today_records` 后按 id 去重合并到本地 |
| 2 | **删除记录不同步（双向）** | 两端 | 合并策略只按 id 去重追加，不会删除本地有但云端没有的记录 | 改为"以云端为准"：本地有但云端没有的记录 → 删掉 |

### 🟡 P1 — 推送字段缺失

| # | 问题 | 归属 | 说明 |
|---|------|------|------|
| 3 | **推送缺少 `week_ml` / `month_ml`** | 手表端 | `syncTodayToLeaderboard()` 只带了 `today_ml`，排行榜切到"本周/本月"时手表用户数据缺失 |
| 4 | **推送缺少 `pill_today`** | 两端 | 吃药标记字段未推送，双方均不显示 |
| 5 | **拉取缺少 delete 同步** | 手表端 | 合并时只追加不删除，见 P0 #2 |

### 🟠 P2 — 代码质量

| # | 问题 | 说明 |
|---|------|------|
| 6 | **硬编码 Supabase 凭证** | `loadSupabaseConfig()` 返回写死的 URL 和 Key，无法灵活切换 |
| 7 | **大量重复代码** | `getAnonUserId`、`loadSettings`、`saveRecords`、`showModal` 等工具函数与 Web 版重复，改了一端容易忘另一端 |

### 🔵 P3 — 架构

| # | 问题 | 说明 |
|---|------|------|
| 8 | **共用 `user_id` 无法区分来源** | 手机和手表使用同一 `user_id`，无法区分一条记录是手机还是手表写的 |
| 9 | **WebView 版本过旧** | Oppo Watch 2 的 WebView 为 Chrome 61（2017），不支持 ES6+、`fetch()`、`crypto.randomUUID()` 等现代 API |

---

## 四、调试工具

### 4.1 手表端同步调试面板

设置页"立即同步"按钮下方有可展开/收起的调试面板，同步时实时显示每一步的日志，格式与手机端统一。

日志行格式对照表：

| 场景 | 日志 |
|------|------|
| 同步开始 | `🔄 开始同步...` |
| 用户标识 | `用户 ID: ad639ebb...` |
| 本地状态 | `📱 手表本地: 3 条, 750ml` |
| 拉取开始 | `📥 拉取云端记录合并到本地...` |
| 拉取请求 | `🔍 GET .../rest/v1/leaderboard?...` |
| 拉取状态 | `🔍 HTTP 200 OK` |
| 拉取结果 | `🔍 返回 today_records: 2 条` |
| 拉取详情 | `🔍 记录详情: abc123=500ml😊, def456=1000ml` |
| 推送信息 | `📤 推送 3 条, 总量 1250ml, 连续 2 天` |
| 推送状态 | `🌐 HTTP 200 ✅ 推送成功` |
| 合并对比 | `📥 本地已有 2 条, 云端 3 条` |
| 合并结果 | `📥 合并了 1 条: xxxyyy` |
| 同步完成 | `✅ 同步完成` |

### 4.2 手表端 user_id 查看

设置 → 连接手机 → 描述行显示 `ID: xxxxxxxx`（前 8 位），用于与手机端对比是否一致。

---

## 五、关键数据

- **Supabase URL**: `https://iqbkxsnanvupsvzckvrs.supabase.co`
- **Anon Key**: `sb_publishable_48GJWDTjOtydQXYEPVQZVQ_sFxPvRYf`
- **user_id（手表）**: `ad639ebb-62d5-4a21-8d34-9186c335b71d`
- **leaderboard 表主键**: `user_id`（已设置）
- **手表 WebView**: Chrome 61.0.3163.98（Android 8.1）

---

## 六、优先修复建议

| 优先级 | 问题 | 归属 | 工作量 |
|--------|------|------|--------|
| P0 | 手机看不到手表数据 | 手机端 | 小 |
| P0 | 删除不同步（两端） | 两端 | 小 |
| P1 | 推送缺少 week_ml / month_ml | 手表端 | 小 |
| P1 | 推送缺少 pill_today | 两端 | 小 |
| P2 | 硬编码凭证 | 手表端 | 极小 |
| P2 | 代码重复 | 两端 | 中 |
| P3 | 无法区分数据来源 | 两端 | 需架构决策 |
| P3 | WebView 版本过旧 | 手表端 | 无法修复（硬件限制） |
