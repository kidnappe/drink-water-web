# 🚰 喝杯水吧 — 会话存档

> 生成时间：2026-06-16
> 项目路径：`D:\project\drink-water-web\`
> 部署地址：`https://kidnappe.github.io/drink-water-web/`
> 仓库：`kidnappe/drink-water-web`（GitHub Pages）

---

## 一、项目架构

### 文件结构

```
D:\project\drink-water-web\
├── index.html              ← 全部功能（单页 HTML，~810KB JS+CSS）
├── manifest.json           ← PWA 配置
├── sw.js                   ← Service Worker（网络优先，离线回退）
├── CHANGELOG.md            ← 版本更新日志
├── schema.sql              ← 数据库建表脚本
├── migrate-pk-to-user_id.sql       ← 迁移：主键从 id 改 user_id
├── migrate-week-month-ml.sql       ← 迁移：加 week_ml/month_ml
├── icons/                  ← 应用图标（SVG + PNG）
│   ├── Icon-192.svg        ← SVG 矢量图标（天蓝底 + 白色水杯）
│   ├── Icon-512.svg
│   ├── Icon-maskable-*.svg
│   └── Icon-*.png
├── backup/                 ← 版本备份
│   ├── f42bdb2-移除SDK和Chart.js后.html
│   └── v1.4.1-完整版-用户提供.html
└── .gitattributes          ← 强制 UTF-8 编码
```

### 技术栈

- 纯 HTML + CSS + JavaScript（零框架、零外部 CDN 依赖）
- Canvas API — 统计图表（自绘柱状图）
- Supabase — 可选在线排行榜（通过原生 `fetch` 通信）
- PWA Manifest + Service Worker — 支持添加到手机桌面 + 离线可用
- SVG 应用图标

---

## 二、当前版本状态

**最新版本：** v1.5.1（问题修复与优化）

**最新提交：** `c6b40fb` — Update CHANGELOG.md

**本地分支：** `main`
**远程分支：** `origin/main`
**状态：** 领先远程 0 个提交（已同步）

---

## 三、全部功能清单

### 首页
- [x] 每日喝水记录（增删改）
- [x] 250 / 350 / 500 / 1000ml 一键记录
- [x] 自定义水量（设置可调）
- [x] 👅 喝一小口（sipAmount，1-50ml 可调，默认 15ml）
- [x] 💊 吃药提醒（首页 💊 按钮，点击记录吃药）
- [x] 养成花园（植物成长，连续达标解锁装饰）
- [x] 进度跑道（达标小人举旗）
- [x] 撒花庆祝（冷却锁，防堆积）
- [x] 情绪选择（21 种表情，可补选/更换）
- [x] 今日记录列表（折叠、添加心情、删除）

### 排行榜
- [x] 多周期排行（今日 / 本周 / 本月）
- [x] 在线排行榜（Supabase 可选配置）
- [x] 按需拉取明细（点击头像查看）
- [x] 管理员模式（标题连点 5 次进入）
- [x] 管理员封禁（删除 + 标记 banned）
- [x] 轮询已关闭（改为手动刷新）
- [x] 排行榜说明弹窗
- [x] 排行榜网络日志弹窗
- [x] 排行榜规则弹窗
- [x] 💊 吃药标记（已吃药显示 💊）

### 历史页
- [x] 周/月柱状图（Canvas 自绘，月份标签按周间隔显示）
- [x] 统计摘要（日均/最高/最低/达标天数）
- [x] 喝水日历热力图
- [x] 日历日期可点（弹窗查看当日记录，最新在前）
- [x] 日历翻月（◀ ▶）

### 设置
- [x] 每日目标
- [x] 自定义水量 + 喝一小口量
- [x] 喝水提醒（开关 + 周期 1-60 分钟可调）
- [x] 吃药提醒（开关，开启后首页显示 💊 按钮）
- [x] 通知权限自动请求（刷新后恢复）
- [x] 深色模式（跟随系统 / 开 / 关 + auto 按钮）
- [x] 深色模式完整 CSS（40+ 条规则）
- [x] 头像昵称（50 种动物）
- [x] 在线排行榜配置（Supabase URL + Key）
- [x] 连接手表（生成配对码）
- [x] 断开手表（跳过拉取，仅推送排行）
- [x] 同步按钮（灰色禁用，改为自动推送）
- [x] 导出/导入数据（JSON，try/finally）
- [x] 数据重置
- [x] 关于应用（更新日志弹窗）
- [x] 特效说明
- [x] 版本号统一管理（`APP_VERSION` 变量）

### 数据同步
- [x] 攒批推送（30 秒合并，不多次 HTTP 请求）
- [x] 累计值缓存（今日/本周/本月，增量更新）
- [x] 网络监听自动推送（离线→联网自动补推）
- [x] Service Worker（网络优先，离线回退）
- [x] SW 更新机制（改 `SW_DEPLOY` 值触发更新）
- [x] 合并云端记录日期过滤（只合并当日）

### 技术特性
- [x] CSS 变量化（`:root --primary`，改主题色只改一处）
- [x] localStorage key 常量化（`LS` 对象）
- [x] DOM 查询缓存（`$()` 辅助函数）
- [x] 零外部 CDN 依赖（全部原生 fetch）
- [x] XSS 防范（data- 属性 + 事件委托）
- [x] calcStreak 日期映射表优化
- [x] PWA 安装支持（manifest + SW）

---

## 四、版本历史

| 版本 | 标题 | 主要内容 |
|------|------|---------|
| v1.5.1 | 问题修复与优化 | 排行榜、吃药记录、日期颜色、排序修复 |
| v1.5.0 | 吃药提醒 | 💊 吃药记录、排行榜药丸标记、记录混排 |
| v1.4.2 | 问题修复 | PWA 图标、SVG 图标、排行榜控件、多处修复 |
| v1.4.1 | 自动同步 + 离线支持 | 网络监听自动推送、SW 离线支持 |
| v1.4.0 | 日历交互 + 深色模式 | 日历翻月、日期可点、深色模式、提醒周期 |
| v1.3.0 | 零外部依赖 + 同步稳定 | 移除 CDN、Safari 修复、409 修复 |
| v1.2.3 | 体验优化 | 心情帮助、记录折叠、整行可点 |
| v1.2.2 | 补选心情 | 补选/更换心情 |
| v1.2.0 | 情绪记录 | 喝水选情绪、查看他人记录 |
| v1.1.1 | 排行榜刷新按钮 | 刷新冷却 |
| v1.1.0 | Supabase 在线排行榜 | 在线排行、PWA 安装 |
| v1.0.0 | 初始版本 | 喝水记录、花园、排行、图表 |

---

## 五、数据库（Supabase）

### leaderboard 表

```sql
user_id TEXT PRIMARY KEY,
nickname TEXT DEFAULT '',
avatar_idx INTEGER DEFAULT 0,
today_ml INTEGER DEFAULT 0,
week_ml INTEGER DEFAULT 0,
month_ml INTEGER DEFAULT 0,
streak INTEGER DEFAULT 0,
today_records JSONB DEFAULT '[]'::jsonb,  -- [{id, amount, time, mood, type}]
banned BOOLEAN DEFAULT FALSE,
pill_today BOOLEAN DEFAULT FALSE,         -- v1.5.0 新增
updated_at TIMESTAMPTZ DEFAULT NOW()
```

### device_pairing 表

```sql
pairing_code TEXT PRIMARY KEY,
user_id TEXT NOT NULL,
created_at TIMESTAMPTZ DEFAULT NOW()
```

### Supabase 凭证
- **URL**: `https://iqbkxsnanvupsvzckvrs.supabase.co`
- **Anon Key**: `sb_publishable_48GJWDTjOtydQXYEPVQZVQ_sFxPvRYf`
- **手机端 user_id**: `ad639ebb-62d5-4a21-8d34-9186c335b71d`

### 待执行迁移

```sql
ALTER TABLE leaderboard ADD COLUMN pill_today BOOLEAN DEFAULT FALSE;
```

---

## 六、当前已知问题

| 优先级 | 问题 | 说明 |
|--------|------|------|
| P0 | 手机端看不到手表推送的数据 | 合并 `mergeCloudTodayRecords()` 后返回 0 条 |
| P1 | 两端删除操作不同步 | 合并策略只追加不删除 |
| P3 | emoji 统一显示 | Twemoji 字体已从网络消失，Noto Color Emoji 可作为替代 |
| P3 | PWA 安装测试 | 电脑端可安装，手机端需要验证 |

---

## 七、emoji 统一方案探索记录（重要）

**经过多次尝试，结论：Twemoji 字体文件已从所有 CDN 和 GitHub 仓库中消失。**

尝试过的方案及结果：

| 方案 | 尝试 | 状态 |
|------|------|------|
| Twemoji 字体 @font-face | CDN 地址全部无效（404/重定向） | ❌ |
| Twemoji JS 替换 emoji 为 SVG | 闪烁问题，且手机端未生效 | ❌ |
| Noto Color Emoji 字体（Google Fonts） | 已验证可访问，无闪烁 | ✅ 可行但未启用 |
| Twemoji 字体自托管 | 无法下载字体文件 | ❌ |
| 备用方案 | 使用 `font-display: swap` 加载 Noto Color Emoji | 🟡 待用户确认 |

**推荐方案：** Noto Color Emoji（Google Fonts）
```css
@import url('https://fonts.googleapis.com/css2?family=Noto+Color+Emoji&display=swap');
body { font-family: 'Noto Color Emoji', -apple-system, 'Segoe UI', Roboto, sans-serif; }
```

---

## 八、关键架构决策

| 决策 | 内容 |
|------|------|
| 存储 | localStorage（本地）+ Supabase（云端） |
| 数据单元 | 每条记录有唯一 id（UUID），`{id, amount, type, time, mood, dateKey}` |
| 同步策略 | 攒批推送 + 网络监听自动补推 |
| 排行榜 | 按今日/本周/本月累计值排序，按需拉取明细 |
| 用户绑定 | 手机生成 6 位配对码存在 device_pairing 表 |
| 设备标识 | 手机和手表共用同一 user_id |
| Cycle | 提交前必须问用户「要不要写进 CHANGELOG」 |

---

## 九、Service Worker

- 文件：`sw.js`
- 策略：网络优先，离线回退
- 更新：改 `SW_DEPLOY` 值（`index.html` 末尾）触发缓存更新
- 注册：`navigator.serviceWorker.register('sw.js?v=' + SW_DEPLOY)`

---

## 十、备忘

- 所有代码在 `index.html` 单文件中
- 版本号统一由 `APP_VERSION` 变量管理（文件开头）
- 设置页版本号由 `settingVersionDesc` 元素显示
- CHANGELOG 必须用 **UTF-8 无 BOM** 编码
- 备份文件存放在 `backup/` 目录
- 每次改完独立功能建议立即提交，不要攒一堆
