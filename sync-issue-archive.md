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

### 3.1 手机端看不到手表推送的数据

| 维度 | 值 |
|------|-----|
| **状态** | ❌ 未解决 |
| **归属** | **手机端** |
| **症状** | 手表推送 `HTTP 200` 确认成功，云端有数据，但手机同步后 `today_records` 返回 0 条 |
| **可能原因** | 手机端没有实现拉取 `today_records` 合并到本地的逻辑；或手机查询的 `user_id` 与手表不一致 |
| **修复方向** | 手机端同步时执行：`GET /rest/v1/leaderboard?select=today_records&user_id=eq.{uid}` → 按 id 去重合并 → 刷新首页 |

### 3.2 手表端拉不到手机的删除操作

| 维度 | 值 |
|------|-----|
| **状态** | ❌ 未解决 |
| **归属** | **手表端** |
| **症状** | 手机删除了一条记录，同步后云端 `today_records` 已没有该记录，但手表本地还留着 |
| **原因** | 当前合并策略是**按 id 去重追加**，不会删除本地有但云端没有的记录 |
| **修复方向** | 拉取合并时，以云端 `today_records` 为准，把本地今日记录中**不在云端列表里的记录删掉** |

**手表端修复方案**（`syncTodayToLeaderboard` 的合并步骤）：

```javascript
// 合并时：先删掉本地有但云端没有的记录
var cloudIds = {};
cloudRecs.forEach(function(cr){ cloudIds[cr.id] = true; });

// 遍历本地今日记录，不在云端的删掉
var newRecords = [];
gState.records.forEach(function(r){
  if(r.dateKey === todayKey && !cloudIds[r.id]){
    // 这条记录云端已删除，不保留
  } else {
    newRecords.push(r);
  }
});
gState.records = newRecords;

// 然后再做追加
cloudRecs.forEach(function(cr){
  if(!cr.id || localKeys[cr.id]) return;
  gState.records.push({ id:cr.id, amount:cr.amount, ... });
});
```

### 3.3 手机端拉不到手表的删除操作

| 维度 | 值 |
|------|-----|
| **状态** | ❌ 未解决 |
| **归属** | **手机端** |
| **症状** | 同 3.2，方向相反 |
| **修复方向** | 手机端同步时使用同样的"以云端为准覆盖本地"策略 |

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

## 六、当前优先修复项

| 优先级 | 问题 | 归属 | 预估工作量 |
|--------|------|------|-----------|
| P0 | 手机端看不到手表数据 | 手机端 | 小（加拉取合并逻辑） |
| P1 | 手表拉不到手机删除 | 手表端 | 小（合并时加删除） |
| P1 | 手机拉不到手表删除 | 手机端 | 小（同手表方案） |
