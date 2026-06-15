# 💧 喝水记录（网页版）

一个好看又好用的喝水记录工具。纯 HTML/CSS/JavaScript，打开即用，零外部依赖。

---

## 📱 功能一览

### 首页

| 功能 | 说明 |
|------|------|
| 🪴 **养成花园** | 连续达标天数越多，植物从种子→嫩芽→开花不断成长 |
| 🏃 **进度跑道** | 可视化今日喝水进度，达标签小人会举起旗帜 🏁 |
| 🎯 **一键记录** | 快速按钮：250ml / 350ml / 500ml / 1000ml / 自定义 + 👅 15ml 小口 |
| ✏️ **记录管理** | 查看今日喝水记录，可删除单条 |
| 🎉 **撒花庆祝** | 达标自动触发彩纸特效 + 水杯发光 + 植物弹跳 |

### 排行榜 🏆

| 功能 | 说明 |
|------|------|
| 👤 **自定义头像** | 50 种动物头像 + 自定义昵称 |
| 📊 **多周期排行** | 今日 / 本周 / 本月（自动累计） |
| 🌐 **在线排行** | 配合 Supabase 可多人实时排行（可选配置） |
| 🔥 **连击显示** | 连续达标天数用火焰图标展示 |
| 🔧 **管理员模式** | 排行榜标题连点 5 次进入，可管理异常数据 |

### 历史统计 📊

| 功能 | 说明 |
|------|------|
| 📈 **周/月图表** | 柱状图展示每日喝水量，红色虚线标记目标线（Canvas 自绘） |
| 📋 **统计摘要** | 日均、最高、最低喝水量，达标天数 |
| 📅 **喝水日历** | 月度热力图，水量越高填充越满，当天高亮 |
| 👆 **日历日期可点** | 点击日历上任一天，弹窗查看当日每条喝水记录 |
| ◀▶ **日历翻月** | 切换显示不同月份的喝水日历 |

### 设置 ⚙️

| 功能 | 说明 |
|------|------|
| 🎯 **每日目标** | 自定义（默认 2000ml） |
| 💧 **自定义水量** | 设置快捷按钮的默认水量 |
| 🔔 **喝水提醒** | 浏览器通知，可自定义间隔（1-60 分钟，默认 30 分钟） |
| 🌙 **深色模式** | 跟随系统 / 强制开启 / 关闭（auto 一键切换） |
| 👤 **头像昵称** | 从 50 种动物中选择头像，自定义昵称 |
| 🌐 **在线排行榜** | 配置 Supabase 后可实现多人实时排行 |
| 📤 **数据导出/导入** | JSON 格式备份与恢复 |
| 🗑️ **数据重置** | 一键清除所有数据 |
| ⌚ **断开手表** | 暂停与配对设备的数据同步 |
| 📋 **更新日志** | 关于页内置滚动版更新日志 |

### 🎨 视觉特效

- 水杯水面波浪动画
- 气泡上浮效果
- 水满溢出 + 水滴飞溅
- 目标达成撒花 + 星光特效
- 水杯发光脉冲
- 水杯摇晃（超额时）
- 达标进度条小人奔跑动画
- 连续超额解锁：⭐ 星星 → 🦋 蝴蝶 → 👑 皇冠 → 🌈 彩虹
- 深色模式完整配色

---

## 🚀 快速开始

### 直接打开

在线版：[https://kidnappe.github.io/drink-water-web/](https://kidnappe.github.io/drink-water-web/)

或把 `index.html` 下载到本地扔到浏览器即可使用。所有数据保存在浏览器本地。

### 开启在线排行榜（可选）

1. 去 [supabase.com](https://supabase.com) 免费注册一个账号
2. 创建项目 → 在 SQL Editor 执行：

```sql
CREATE TABLE leaderboard (
  user_id TEXT PRIMARY KEY,
  nickname TEXT DEFAULT '',
  avatar_idx INTEGER DEFAULT 0,
  today_ml INTEGER DEFAULT 0,
  week_ml INTEGER DEFAULT 0,
  month_ml INTEGER DEFAULT 0,
  streak INTEGER DEFAULT 0,
  today_records JSONB DEFAULT '[]'::jsonb,
  banned BOOLEAN DEFAULT FALSE,
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE device_pairing (
  pairing_code TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW()
);
```

3. 在 Settings → API 中找到 Project URL 和 anon public key
4. 打开网页 → 设置 → 在线排行榜 → 填写并测试连接

> 两个人填同一个 Supabase 项目，排行榜数据就共享了。

---

## 🛠️ 技术栈

- 纯 HTML + CSS + JavaScript（无框架，零外部依赖）
- Canvas API — 统计数据图表（自绘，无 Chart.js）
- Supabase — 可选在线排行榜数据库（通过原生 fetch 通信）
- PWA Manifest — 支持添加到手机桌面
- SVG — 应用图标

---

## 📄 文件结构

```
├── index.html            ← 全部页面、样式、逻辑（单文件）
├── manifest.json         ← PWA 配置
├── CHANGELOG.md          ← 版本更新日志
├── schema.sql            ← 数据库建表脚本
├── migrate-pk-to-user_id.sql       ← 数据库迁移脚本
├── migrate-week-month-ml.sql       ← 数据库迁移脚本
├── icons/                ← 应用图标
└── README.md             ← 本文件
```

---

## 🤝 更新功能

直接改 `index.html`，改完提交推送到 GitHub 仓库即可。GitHub Pages 会自动部署，等 1-2 分钟刷新就看到新版本。

每次提交前记得问我：「要不要写进更新日志？」
