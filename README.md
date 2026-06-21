# 喝杯水吧 — Oppo Watch 2 移植版

将 Web 版喝水记录应用（[原项目](https://github.com/kidnappe/drink-water-web)）移植为 Oppo Watch 2 可安装的 APK。

> 本仓库是 `watch` 分支，与 Web 版主分支共享同一个 GitHub 仓库。
> Web 版请切到 `master`（或 `main`）分支查看。

---

## 功能总览

### 喝水记录
- **快速加水**：250ml / 350ml / 500ml / 1000ml 一键记录
- **自定义水量**：输入任意毫升数
- **今日进度**：环形进度条 + 达标庆祝动画
- **今日记录列表**：按时间倒序展示每次喝水的量、时间和心情
- **删除单条记录**：右滑或点击删除按钮

### 心情标注
- 14 种表情：💪🥰😊😌😄🤔😴😢😭😡🥳🤗😷🥺
- 喝水前点选心情，该条记录会带上对应表情

### 目标系统
- 每日目标：500ml ~ 5000ml 可调（默认 2000ml）
- 达标进度条实时更新
- 连续达标天数统计（花园页）

### 花园（连续达标激励）
- 7 天：🌱 发芽
- 15 天：🌿 小苗
- 30 天：🌳 小树
- 60 天：🌲 大树
- 90 天：🌸 开花
- 120 天：🌻 结果
- 180 天：🌴 大树 + 🦋 蝴蝶
- 365 天：🎄 终极 + ⭐ 星星

### 统计图表
- **周趋势**：最近 7 天每日水量柱状图
- **月趋势**：最近 30 天每日水量柱状图
- 自绘 Canvas，无任何外部图表库依赖

### 在线排行榜（Supabase）
- 配置 Supabase URL + Anon Key 即可启用
- 每日自动同步 `today_ml` 和 `today_records` 到云端
- 排行榜展示 TOP 20：排名、头像、昵称、今日水量、连续天数
- 金银铜牌样式 + 自己高亮
- 数据双向同步：手机端和手表端数据按 id 去重合并

### 手机配对
- 通过 6 位配对码绑定手机端身份
- 绑定后两端共享同一个 `user_id`，数据自动合并
- 手机端为身份权威（头像、昵称以手机端为准）

### 喝水提醒（手表原生通知）
- 使用 Android `AlarmManager` 定时通知
- 可设间隔：30 / 60 / 90 / 120 分钟
- 可设起止时段（默认 8:00~22:00）
- 振动 + 通知栏提醒
- 需要授予通知权限（Android 13+ 自动弹出请求）

### 深色模式
- 四档循环：跟随系统 → 强制深色 → 浅色（白色背景）→ 定时切换
- 定时模式：设置亮色起始和深色起始时间（默认 6:00~18:00 浅色）
- 浅色模式适用日间手表阅读

### 数据管理
- 导出数据（JSON 文件）
- 导入数据（从 JSON 恢复）
- 所有数据存储在 `localStorage`，与 Web 版完全兼容

### 调试工具
- 同步调试面板：每次同步显示完整日志（可展开/收起）
- 请求日志查看器：记录最近 20 条网络请求
- 连接状态指示器：Supabase 连接状态 + 配对状态 + `user_id` 显示

---

## 与 Web 版的差异

| 项目 | Web 版 | 手表版 |
|------|--------|--------|
| **平台** | PWA / 浏览器 | Android APK（WebView 壳工程） |
| **屏幕** | 手机竖屏 480px+ | 方形 402×576（Oppo Watch 2）|
| **布局** | 自适应 | 固定沉浸式全屏 |
| **主题** | 浅色 + 深色 | 默认深色 OLED 省电 + 四档切换 |
| **图表** | Chart.js（CDN） | Canvas 自绘（零依赖） |
| **底部导航** | 5 个 Tab | 5 个 Tab（排行榜为独立页） |
| **离线存储** | Service Worker | WebView 本地加载 |
| **触控体验** | 标准 | 大按钮 + 60ms 振动反馈 |
| **通知提醒** | 浏览器 Notification API | AlarmManager + NotificationChannel |
| **Supabase** | JS Client SDK（CDN） | XMLHttpRequest + REST API（无 CDN） |
| **数据同步** | 拉→合→推 | 拉→合→推（逻辑一致） |
| **构建部署** | 无构建，直接部署 | Gradle 构建 APK |
| **代码组织** | 单 HTML 文件 | `index.html` + Android Java 原生代码 |

### 手表版独有的功能

| 功能 | 说明 |
|------|------|
| **振动反馈** | 每次点击水按钮触发 60ms 短振 |
| **原生喝水提醒** | AlarmManager 定时通知，纯前端无法实现 |
| **调试日志面板** | 实时显示同步请求/响应详情 |
| **一键断开云连接** | 清除本地 Supabase 配置 |
| **崩溃自诊** | 应用闪退后下次启动直接显示崩溃原因 |

### Web 版独有的功能

| 功能 | 说明 |
|------|------|
| **PWA 安装** | 可添加到手机桌面 |
| **Service Worker** | 离线缓存支持 |
| **手机端首页** | 每日详情、连续天数等手表端已适配 |
| **管理员功能** | 用户管理、数据清理（手表端不可用） |

---

## 项目结构

```
drink-water-watch/
├── index.html                 # 手表版主页面（自包含 HTML + CSS + JS）
├── sync-issue-archive.md      # 同步问题排查文档
├── README.md
├── .gitignore
│
├── android/                   # Android WebView 壳工程
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradle.properties
│   ├── gradlew.bat
│   ├── create-keystore.bat    # 生成 release 签名密钥
│   ├── app/
│   │   ├── build.gradle
│   │   ├── proguard-rules.pro
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       ├── assets/www/index.html    ← 手表版 HTML（与根目录同步）
│   │       ├── java/com/drinkwater/watch/
│   │       │   ├── MainActivity.java    # WebView 入口 + 全屏沉浸
│   │       │   ├── ReminderReceiver.java # AlarmManager 闹钟广播接收器
│   │       │   ├── ReminderBridge.java   # JS ↔ Java 原生桥接
│   │       │   └── CrashHandler.java    # 全局崩溃抓取器
│   │       └── res/
│   │           ├── values/themes.xml
│   │           ├── values/strings.xml
│   │           ├── drawable/ic_launcher_*.xml
│   │           └── mipmap-anydpi-v26/ic_launcher.xml
│   └── gradle/wrapper/
```

---

## 构建与安装

### Debug APK（快速测试）

```bash
cd android
./gradlew assembleDebug
# APK → app/build/outputs/apk/debug/app-debug.apk
```

### Release APK（可分发）

```bash
cd android
# 首次需生成密钥
create-keystore.bat
# 构建已签名 APK
./gradlew assembleRelease
# APK → app/build/outputs/apk/release/app-release.apk
```

### 安装到 Oppo Watch 2

1. 开启手表「开发者选项」和「USB 调试」
   - 设置 → 关于手表 → 连续点击版本号 7 次
   - 设置 → 系统 → 开发者选项 → USB 调试
2. 连接 WiFi 调试：`adb connect <手表IP>:5555`
3. 安装：`adb install app-debug.apk`
4. 或通过 Oppo 手机「手表管理」→「应用安装」侧载

---

## 数据兼容

手表版与 Web 版使用相同的 `localStorage` Key，数据格式完全兼容：

| Key | 说明 | 类型 |
|-----|------|------|
| `drink_water_data` | 喝水记录（JSON 数组） | 互通 |
| `drink_water_settings` | 用户设置（目标、自定义水量等） | 互通 |
| `drink_water_supabase` | Supabase URL + Key | 互通 |
| `drink_water_user_id` | 匿名用户 ID（UUID v4） | 互通 |
| `drink_water_reminder` | 提醒配置（手表独有） | 手表独有 |
| `drink_water_dark_timer` | 定时深浅切换配置（手表独有） | 手表独有 |
| `lb_animal_idx` / `lb_nickname` | 头像/昵称 | 互通（手机端为权威） |
| `lb_paired_with_phone` | 配对状态标记 | 手表独有 |

可通过设置页的「导出/导入数据」在两端间迁移数据。

---

## 技术栈

| 层 | 技术 |
|---|------|
| **前端** | 原生 HTML5 + CSS3 + JavaScript（ES5，兼容 Chrome 61） |
| **图表** | Canvas 2D API 自绘（无 Chart.js 依赖） |
| **后端** | Supabase（PostgreSQL + REST API） |
| **原生壳** | Android 8.1+（API 27），AppCompatActivity + WebView |
| **提醒** | AlarmManager + NotificationChannel |
| **构建** | Gradle 8.2.2 + Android SDK 34 |
| **状态管理** | localStorage + 全局 `gState` 对象 |
---

## 已知问题与待修复项

### 🔴 P0 — 数据同步

| 问题 | 归属 | 根因 | 修复方向 |
|------|------|------|---------|
| **手机端看不到手表推送的数据** | 手机端 | 手机合并逻辑未正确识别手表推送的 `today_records`（可能 time 字段格式或 id 冲突） | 手机端拉取后按 id 去重合并到本地 |
| **删除记录不同步（双向）** | 两端 | 合并策略只按 id 去重追加，不会删除本地有但云端没有的记录 | 改为"以云端为准"：本地有的、云端没有的 → 删掉 |

### 🟡 P1 — 推送字段缺失

| 问题 | 归属 | 说明 |
|------|------|------|
| **推送缺少 `week_ml` / `month_ml`** | 手表端 | `syncTodayToLeaderboard()` 只带了 `today_ml`，排行榜切到"本周/本月"时手表用户数据缺失 |
| **推送缺少 `pill_today`** | 两端 | 吃药标记字段未推送，手机端和手表端均不显示 |
| **拉取缺少 delete 同步** | 手表端 | 合并时只追加不删除，见 P0 |

### 🟠 P2 — 代码质量

| 问题 | 说明 |
|------|------|
| **硬编码 Supabase 凭证** | `loadSupabaseConfig()` 返回写死的 URL 和 Key，无法灵活切换 |
| **大量重复代码** | `getAnonUserId`、`loadSettings`、`saveRecords`、`showModal` 等工具函数与 Web 版重复，改了一端容易忘另一端 |

### 🔵 P3 — 架构

| 问题 | 说明 |
|------|------|
| **共用 `user_id` 无法区分数据来源** | 手机和手表使用同一 `user_id`，调试时不知道一条记录是手机还是手表写的 |
| **WebView 版本过旧** | Oppo Watch 2 的 WebView 为 Chrome 61（2017），不支持 ES6+、`fetch()`、`crypto.randomUUID()`等现代 API，需大量 polyfill |

---

## Git 分支说明

本仓库有两个分支：

```
master / main  — Web 版（PWA，手机浏览器用）
watch          — 手表版（Android APK，Oppo Watch 2 用）
```

两个分支共享同一个 Supabase 项目和数据库表结构，通过 `user_id` 配对实现数据互通。
