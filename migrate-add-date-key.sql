-- ============================================================
-- 迁移：添加 date_key 列
-- 用途：让排行榜只显示当天数据，过滤旧数据
-- 使用方法：在 Supabase SQL Editor 粘贴执行
-- ============================================================

-- 1. 添加 date_key 列（已有行默认空字符串，不会被查询命中）
ALTER TABLE leaderboard ADD COLUMN IF NOT EXISTS date_key TEXT NOT NULL DEFAULT '';

-- 2. 可选：为已有行回填日期（从 updated_at 提取）
--    如果希望某天的旧数据也能通过 date_key 查询到，可以执行：
-- UPDATE leaderboard SET date_key = LEFT(updated_at::text, 10) WHERE date_key = '';
--    注意：updated_at 是 UTC 时间，时区偏差可能导致日期偏移一天
