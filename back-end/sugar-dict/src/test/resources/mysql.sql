-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 用户表
CREATE TABLE `users` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `identifier` VARCHAR(100) NOT NULL COMMENT '用户手机号或邮箱（唯一标识）',
  `code` VARCHAR(100) DEFAULT NULL COMMENT '密码哈希',
  `role` VARCHAR(100) DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_identifier` (`identifier`)
) ENGINE=InnoDB COMMENT='用户基础信息表';


-- 模块/场景表 (统一管理 模块ID 和 场景ID)
CREATE TABLE `content_modules` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '模块ID',
  `name` VARCHAR(100) NOT NULL COMMENT '模块名',
  `description` TEXT COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='场景定义';

-- 单词表
CREATE TABLE `words` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '单词ID',
  `text` VARCHAR(100) NOT NULL COMMENT '单词本身',
  `phonetic_us` VARCHAR(100) DEFAULT NULL COMMENT '音标US',
  `phonetic_uk` VARCHAR(100) DEFAULT NULL COMMENT '音标UK',
  `definition` JSON NOT NULL COMMENT '意思/释义',
  `audio_us_url` VARCHAR(255) DEFAULT NULL COMMENT 'US发音文件地址',
  `audio_uk_url` VARCHAR(255) DEFAULT NULL COMMENT 'UK发音文件地址',
  `phrases` JSON DEFAULT NULL COMMENT '词组 (JSON数组存储)',
  `example_sentences` JSON DEFAULT NULL COMMENT '单词内的简短例句 (JSON)',
  `module_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '所属模块ID',
  PRIMARY KEY (`id`),
  KEY `idx_text` (`text`),
  KEY `idx_module_id` (`module_id`)
) ENGINE=InnoDB COMMENT='单词库';

-- 句子表
CREATE TABLE `sentences` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '句子ID',
  `text` TEXT NOT NULL COMMENT '句子本身',
  `text_translation` TEXT COMMENT '意思/翻译',
  `audio_us_url` VARCHAR(255) DEFAULT NULL COMMENT 'US发音文件地址',
  `audio_uk_url` VARCHAR(255) DEFAULT NULL COMMENT 'UK发音文件地址',
  `module_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '所属模块ID',
  PRIMARY KEY (`id`),
  KEY `idx_module_id` (`module_id`)
) ENGINE=InnoDB COMMENT='句子库';

-- ==========================================
-- 3. 学习记录与词书 (User Learning Data)
-- 对应图中：生词本、错题本、自定义词书
-- ==========================================

-- 生词本 (New Words)
-- 排序：按添加时间
CREATE TABLE `user_unknown_words` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `word_id` BIGINT UNSIGNED NOT NULL,
  `module_id` BIGINT UNSIGNED NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='生词本';

-- 错题本 (Mistakes)
-- 核心逻辑：包含单词和句子，需支持艾宾浩斯遗忘曲线
CREATE TABLE `user_mistakes` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `item_type` VARCHAR(100) NOT NULL COMMENT '错题类型',
  `word_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '如果是单词',
  `sentence_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '如果是句子',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='错题本(含遗忘曲线算法字段)';

-- 自定义词书 (Custom Book)
CREATE TABLE `user_custom_books` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `name` VARCHAR(50) DEFAULT '默认词书' COMMENT '词书名称',
  `word_id` BIGINT UNSIGNED DEFAULT NULL,
  `sentence_id` BIGINT UNSIGNED DEFAULT NULL,
  `module_id` BIGINT UNSIGNED NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='用户自定义词书';

-- ==========================================
-- 4. 进度管理 (Progress)
-- 对应图中：蓝色学习进度、个人总进度
-- ==========================================

-- 场景/模块维度的学习进度
CREATE TABLE `user_words_progress` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `module_id` BIGINT UNSIGNED NOT NULL,
  `total_words_count` INT DEFAULT 0 ,
  `learned_words_count` INT DEFAULT 0 ,
  `last_studied_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_words_progress` (`user_id`, `module_id`)
) ENGINE=InnoDB;

CREATE TABLE `user_learned_words` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `module_id` BIGINT UNSIGNED NOT NULL ,
  `word_id` BIGINT UNSIGNED NOT NULL,
  `learned_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_learned_words` (`user_id`, `word_id`),
  INDEX `idx_user_learned_words` (`user_id`, `module_id`)
) ENGINE=InnoDB;

CREATE TABLE `user_sentences_progress` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `module_id` BIGINT UNSIGNED NOT NULL,
  `total_sentences_count` INT DEFAULT 0 ,
  `learned_sentences_count` INT DEFAULT 0 ,
  `last_studied_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_sentences_progress` (`user_id`, `module_id`)
) ENGINE=InnoDB;

CREATE TABLE `user_learned_sentences` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `module_id` BIGINT UNSIGNED NOT NULL ,
  `sentence_id` BIGINT UNSIGNED NOT NULL,
  `learned_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_learned_sentences` (`user_id`, `sentence_id`),
  INDEX `idx_user_learned_sentences` (`user_id`, `module_id`)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
