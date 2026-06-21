# 喝杯水吧 — Oppo Watch 2 移植版

将 Web 版喝水记录应用移植为 Oppo Watch 2 可安装的 APK。

## 项目结构

```
watch-app/
├── index.html              # 手表版主页面 (自包含 HTML + CSS + JS)
├── android/                # Android WebView 壳工程
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradle.properties
│   ├── create-keystore.bat  # 生成 release 签名密钥
│   ├── app/
│   │   ├── build.gradle
│   │   ├── proguard-rules.pro
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       ├── assets/www/index.html  ← 手表版 HTML (同根目录)
│   │       ├── java/com/drinkwater/watch/
│   │       │   ├── MainActivity.java
│   │       │   ├── ReminderReceiver.java   # 闹钟广播接收器
│   │       │   └── ReminderBridge.java     # JS 原生桥接
│   │       └── res/
```

## 核心变更 (对比 Web 版)

| 项目 | Web 版 | 手表版 |
|------|--------|--------|
| 布局 | 手机竖屏 480px 宽 | 方形 402×576 沉浸式 |
| 主题 | 浅色 + 深色 | 默认深色 OLED 省电 |
| 图表 | Chart.js (CDN) | Canvas 自绘 (无依赖) |
| 菜单 | 5 个底部 Tab | 5 个 Tab (含排行榜) |
| 离线 | Service Worker | WebView 本地加载 |
| 触控 | 默认 | 大按钮 + 振动反馈 |
| 提醒 | 浏览器通知 | AlarmManager + 通知 |
| Supabase | JS Client SDK | fetch REST API (无 CDN) |
| 文件 | 无构建 | APK 可安装到手表 |

## 功能清单

- [x] 喝水记录 (250/350/500/1000ml + 自定义)
- [x] 心情标注 (14 种表情)
- [x] 今日目标进度 (进度条 + 达标提示)
- [x] 连续达标 (花园植物成长 + 装饰)
- [x] 周/月统计图表 (自绘 Canvas)
- [x] 每日目标设置
- [x] 深色模式
- [x] 头像/昵称
- [x] 数据导入/导出
- [x] Supabase 在线排行榜
- [x] **排行榜独立 Tab** (排名列表 + 金银铜牌 + 自己高亮)
- [x] **喝水提醒** (定时通知，可设间隔/时段)
- [x] **振动触控反馈** (按钮点击短振)
- [x] Supabase REST API (无需 CDN 加载库)

## 构建 APK 步骤

### Debug APK (快速测试)

```bash
cd watch-app/android
./gradlew assembleDebug
# APK 输出: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (可分发)

```bash
cd watch-app/android
# 1. 生成签名密钥 (仅首次)
create-keystore.bat

# 2. 构建已签名 APK
./gradlew assembleRelease
# APK 输出: app/build/outputs/apk/release/app-release.apk
```

### Android Studio

1. 打开 Android Studio → `File` → `Open` → 选择 `watch-app/android/`
2. 等待 Gradle 同步完成
3. 选择 `Build` → `Build Bundle(s) / APK` → `Build APK`

## 安装到 Oppo Watch 2

1. 开启手表的「开发者选项」和「USB 调试」
   - 设置 → 关于手表 → 连续点击版本号 7 次
   - 设置 → 系统 → 开发者选项 → USB 调试
2. 连接 USB 或 WiFi 调试
3. 安装 APK：
   ```bash
   adb connect <手表IP>:5555
   adb install app-debug.apk
   ```
4. 或通过 Oppo 手机「手表管理」→「应用安装」侧载

## 数据兼容

手表版与 Web 版使用相同的 `localStorage` Key：
- `drink_water_data` — 喝水记录
- `drink_water_settings` — 设置
- `drink_water_supabase` — Supabase 配置
- `drink_water_reminder` — 提醒设置

因此数据格式完全兼容，可通过导入/导出功能互通。

## 喝水提醒说明

- 提醒使用 Android `AlarmManager.setInexactRepeating()`，省电可靠
- 可设置间隔 (30/60/90/120 分钟) 和起止时段
- 需要授予通知权限 (Android 13+ 自动弹出请求)
- 提醒仅在起止时段内触发，避免夜间打扰
- 提示：手表需保持与手机蓝牙连接或 Wi-Fi 以确保通知可达

## 开发说明

### 更新前端页面

编辑 `index.html` 后，需要同步到 Android assets：

```bash
copy index.html android/app/src/main/assets/www/index.html
```

> 项目已配置软拷贝机制 —— 每次构建会自动使用根目录的 index.html。
