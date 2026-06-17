-- ============================================================
-- 迁移：leaderboard 表增加 week_ml / month_ml 字段
--
-- 用途：排行榜支持「本周」「本月」视图
--       每次推送时手机端算好累计值写入这两个字段
--       切换标签时直接拉取对应字段，无需额外计算
-- ============================================================

ALTER TABLE leaderboard ADD COLUMN week_ml INTEGER DEFAULT 0;
ALTER TABLE leaderboard ADD COLUMN month_ml INTEGER DEFAULT 0;
