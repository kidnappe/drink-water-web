-- ============================================================
-- 喝杯水吧 — 数据库建表脚本
-- 用途：一次性运行，建立应用所需的全部表
-- 使用方法：在 Supabase SQL Editor 粘贴执行，或通过 psql 导入
-- ============================================================

-- 1. 排行榜 & 数据同步表
--    每用户一条记录，今日数据实时 upsert
--    主键为 user_id，upsert 用 Prefer: resolution=merge-duplicates
--    以 user_id 检测冲突，确保手机↔手表数据正确合并
CREATE TABLE IF NOT EXISTS leaderboard (
  user_id TEXT PRIMARY KEY,
  nickname TEXT DEFAULT '',
  avatar_idx INTEGER DEFAULT 0,
  today_ml INTEGER DEFAULT 0,
  week_ml INTEGER DEFAULT 0,
  month_ml INTEGER DEFAULT 0,
  streak INTEGER DEFAULT 0,
  today_records JSONB DEFAULT '[]'::jsonb,   -- 今日喝水明细 [{id, amount, time, mood}]
  banned BOOLEAN DEFAULT FALSE,               -- 管理员封禁标记
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. 设备配对表
--    手机生成 6 位码存入此表，手表凭码读取 user_id 完成绑定
CREATE TABLE IF NOT EXISTS device_pairing (
  pairing_code TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. 索引（提升查询性能）
CREATE INDEX IF NOT EXISTS idx_leaderboard_today_ml 
  ON leaderboard (today_ml DESC);
CREATE INDEX IF NOT EXISTS idx_pairing_code 
  ON device_pairing (pairing_code);
