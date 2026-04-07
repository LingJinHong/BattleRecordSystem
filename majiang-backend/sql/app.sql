-- =========================================================
-- MaJiang System - 初始化 SQL（全量）
-- 说明：该脚本按“全新初始化”设计，不考虑已部署增量迁移。
-- =========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 若全新初始化，先按依赖顺序删除
DROP TABLE IF EXISTS `game_player`;
DROP TABLE IF EXISTS `game`;
DROP TABLE IF EXISTS `app_team_user`;
DROP TABLE IF EXISTS `app_friend_user`;
DROP TABLE IF EXISTS `app_user_battle`;
DROP TABLE IF EXISTS `app_team_data`;
DROP TABLE IF EXISTS `app_user_data`;

-- 用户数据表
CREATE TABLE `app_user_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openid` VARCHAR(64) NOT NULL COMMENT '小程序用户唯一标识',
  `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '头像访问路径',
  `title` VARCHAR(255) NOT NULL COMMENT '标题/昵称',
  `content` TEXT NOT NULL COMMENT '内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小程序用户数据表';

-- 小队表
CREATE TABLE `app_team_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '创建者用户ID',
  `team_name` VARCHAR(128) NOT NULL COMMENT '小队名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小队信息表';

-- 战绩表（含幂等约束：同一局同一用户仅1条）
CREATE TABLE `app_user_battle` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `income_expense_type` VARCHAR(32) NOT NULL COMMENT '收支类型(income/expense)',
  `venue_fee` INT NOT NULL DEFAULT 0 COMMENT '场地费',
  `score` INT NOT NULL COMMENT '分数/金额（正负）',
  `team_id` BIGINT DEFAULT NULL COMMENT '小队ID',
  `game_id` BIGINT NOT NULL COMMENT '对局ID',
  `match_date` DATE NOT NULL COMMENT '比赛日期',
  `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_game_user` (`game_id`, `user_id`),
  KEY `idx_user_id_create_time` (`user_id`, `create_time`),
  KEY `idx_team_id_match_date` (`team_id`, `match_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户战绩表';

-- 好友关系表
CREATE TABLE `app_friend_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `friend_user_id` BIGINT NOT NULL COMMENT '好友用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_friend` (`user_id`, `friend_user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_friend_user_id` (`friend_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户好友关系表';

-- 小队成员关系表
CREATE TABLE `app_team_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `team_id` BIGINT NOT NULL COMMENT '小队ID',
  `user_id` BIGINT NOT NULL COMMENT '队员用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_user` (`team_id`, `user_id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小队与队员关系表';

-- 对局表
CREATE TABLE `game` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '对局ID',
  `team_id` BIGINT DEFAULT NULL COMMENT '小队ID',
  `creator_id` BIGINT DEFAULT NULL COMMENT '创建人用户ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT '状态：WAITING/FINISHED',
  `game_time` DATETIME DEFAULT NULL COMMENT '开局时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_team_id_created_at` (`team_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对局主表';

-- 对局玩家表
CREATE TABLE `game_player` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `game_id` BIGINT NOT NULL COMMENT '对局ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `score` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '玩家分数（预留）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_game_player` (`game_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对局玩家关联表';

SET FOREIGN_KEY_CHECKS = 1;
