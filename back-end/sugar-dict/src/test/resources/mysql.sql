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
ALTER TABLE `sentences`
ADD COLUMN `title` VARCHAR(255) NULL COMMENT '标题' AFTER `audio_uk_url`;
select * from sentences where id>=4 and id<=14;
UPDATE `sentences` SET `title` = '{"t0":"办银行卡", "t1":"开卡咨询", "t2":"开户"}' WHERE `id` in (1, 2,3);
UPDATE `sentences` SET `title` = '{"t0":"办银行卡", "t1":"开卡咨询", "t2":"材料准备"}' WHERE `id` in (4, 5,6);
UPDATE `sentences` SET `title` = '{"t0":"办银行卡", "t1":"开卡咨询", "t2":"账户类型"}' WHERE `id` in (7,8);
UPDATE `sentences` SET `title` = '{"t0":"办银行卡", "t1":"办卡", "t2":"手机银行"}' WHERE `id` >= 15 and `id` <= 17;
UPDATE `sentences` SET `title` = '{"t0":"办银行卡", "t1":"办卡", "t2":"交易"}' WHERE `id` >= 18 and `id` <= 21;
UPDATE `sentences` SET `title` = '{"t0":"办银行卡", "t1":"售后服务", "t2":"卡片丢失"}' WHERE `id` >= 22 and `id` <= 24;
UPDATE `sentences` SET `title` = '{"t0":"办银行卡", "t1":"售后服务", "t2":"问题与投诉"}' WHERE `id` >= 25 and `id` <= 27;
UPDATE `sentences` SET `title` = '{"t0":"办银行卡", "t1":"信用卡问题", "t2":"NULL"}' WHERE `id` >= 28 and `id` <= 32;
select * from sentences where module_id=1;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"预约", "t2":"预定时间/人数"}' WHERE `id` >= 33 and `id` <= 35;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"预约", "t2":"取消预约"}' WHERE `id` >= 36 and `id` <= 38;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"预约", "t2":"到达"}' WHERE `id` >= 39 and `id` <= 42;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"点餐", "t2":"询问推荐"}' WHERE `id` >= 43 and `id` <= 45;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"点餐", "t2":"点饮品"}' WHERE `id` >= 46 and `id` <= 48;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"点餐", "t2":"特殊饮食需求"}' WHERE `id` >= 49 and `id` <= 51;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"点餐", "t2":"还没准备好点单"}' WHERE `id` >= 52 and `id` <= 54;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"就餐", "t2":"菜品评价"}' WHERE `id` >= 55 and `id` <= 57;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"就餐", "t2":"餐馆评价"}' WHERE `id` >= 58 and `id` <= 60;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"就餐", "t2":"遇到问题"}' WHERE `id` >= 61 and `id` <= 63;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"就餐", "t2":"请求额外物品"}' WHERE `id` >= 64 and `id` <= 66;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"结账", "t2":"要账单"}' WHERE `id` >= 67 and `id` <= 69;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"结账", "t2":"分账"}' WHERE `id` >= 70 and `id` <= 72;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"结账", "t2":"打包"}' WHERE `id` >= 73 and `id` <= 75;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"结账", "t2":"付款方式"}' WHERE `id` >= 76 and `id` <= 78;
UPDATE `sentences` SET `title` = '{"t0":"餐厅点餐", "t1":"结账", "t2":"表达感谢"}' WHERE `id` >= 79 and `id` <= 81;
select * from sentences where module_id=2;
UPDATE `sentences` SET `title` = '{"t0":"机场", "t1":"订票", "t2":"询问信息"}' WHERE `id` >= 150 and `id` <= 152;
UPDATE `sentences` SET `title` = '{"t0":"机场", "t1":"订票", "t2":"预定机票"}' WHERE `id` >= 153 and `id` <= 155;
UPDATE `sentences` SET `title` = '{"t0":"机场", "t1":"订票", "t2":"改签"}' WHERE `id` >= 156 and `id` <= 158;
UPDATE `sentences` SET `title` = '{"t0":"机场", "t1":"启程", "t2":"办理登机"}' WHERE `id` >= 159 and `id` <= 161;
UPDATE `sentences` SET `title` = '{"t0":"机场", "t1":"启程", "t2":"安检"}' WHERE `id` >= 162 and `id` <= 164;
UPDATE `sentences` SET `title` = '{"t0":"机场", "t1":"启程", "t2":"候机"}' WHERE `id` >= 165 and `id` <= 167;
UPDATE `sentences` SET `title` = '{"t0":"机场", "t1":"乘机", "t2":"登机广播"}' WHERE `id` >= 168 and `id` <= 170;
UPDATE `sentences` SET `title` = '{"t0":"机场", "t1":"乘机", "t2":"确认座位"}' WHERE `id` = 171;
select * from sentences where module_id=3;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"预定", "t2":"问房型"}' WHERE `id` >= 172 and `id` <= 175;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"预定", "t2":"问服务"}' WHERE `id` >= 176 and `id` <= 178;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"预定", "t2":"问价格"}' WHERE `id` >= 179 and `id` <= 183;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"预定", "t2":"问押金"}' WHERE `id` >= 184 and `id` <= 186;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"到达", "t2":"接机"}' WHERE `id` >= 187 and `id` <= 189;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"到达", "t2":"办理入住"}' WHERE `id` >= 190 and `id` <= 192;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"到达", "t2":"选房间"}' WHERE `id` >= 193 and `id` <= 195;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"到达", "t2":"去房间"}' WHERE `id` >= 196 and `id` <= 198;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"客房服务", "t2":"换房"}' WHERE `id` >= 199 and `id` <= 201;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"客房服务", "t2":"卫生清洁"}' WHERE `id` >= 202 and `id` <= 204;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"客房服务", "t2":"突发情况"}' WHERE `id` >= 205 and `id` <= 208;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"退房", "t2":"推迟退房"}' WHERE `id` >= 209 and `id` <= 211;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"退房", "t2":"付款"}' WHERE `id` >= 212 and `id` <= 214;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"退房", "t2":"收据发票"}' WHERE `id` >= 215 and `id` <= 217;
UPDATE `sentences` SET `title` = '{"t0":"定酒店", "t1":"退房", "t2":"行李寄存"}' WHERE `id` >= 218 and `id` <= 220;
select * from sentences where module_id=5;
UPDATE `sentences` SET `title` = '{"t0":"课程学习", "t1":"小组作业", "t2":"小组分工"}' WHERE `id` >= 235 and `id` <= 238;
UPDATE `sentences` SET `title` = '{"t0":"课程学习", "t1":"小组作业", "t2":"作业讨论"}' WHERE `id` >= 239 and `id` <= 240;
UPDATE `sentences` SET `title` = '{"t0":"课程学习", "t1":"课程辅导", "t2":"NULL"}' WHERE `id` >= 241 and `id` <= 245;
UPDATE `sentences` SET `title` = '{"t0":"课程学习", "t1":"参加活动", "t2":"NULL"}' WHERE `id` >= 246 and `id` <= 250;
select * from sentences where module_id=6;
UPDATE `sentences` SET `title` = '{"t0":"购物", "t1":"日常购物", "t2":"NULL"}' WHERE `id` >= 82 and `id` <= 86;
UPDATE `sentences` SET `title` = '{"t0":"购物", "t1":"服装店", "t2":"NULL"}' WHERE `id` >= 87 and `id` <= 91;
UPDATE `sentences` SET `title` = '{"t0":"购物", "t1":"数码产品", "t2":"NULL"}' WHERE `id` >= 92 and `id` <= 96;
UPDATE `sentences` SET `title` = '{"t0":"购物", "t1":"药店", "t2":"NULL"}' WHERE `id` >= 92 and `id` <= 101;
UPDATE `sentences` SET `title` = '{"t0":"购物", "t1":"网上购物", "t2":"NULL"}' WHERE `id` >= 102 and `id` <= 106;
UPDATE `sentences` SET `title` = '{"t0":"购物", "t1":"特殊购物需求", "t2":"NULL"}' WHERE `id` >= 107 and `id` <= 111;
select * from sentences where module_id=7;
UPDATE `sentences` SET `title` = '{"t0":"剪头发", "t1":"预约", "t2":"NULL"}' WHERE `id` >= 112 and `id` <= 115;
UPDATE `sentences` SET `title` = '{"t0":"剪头发", "t1":"剪发", "t2":"到店"}' WHERE `id` >= 116 and `id` <= 119;
UPDATE `sentences` SET `title` = '{"t0":"剪头发", "t1":"剪发", "t2":"沟通发型"}' WHERE `id` >= 120 and `id` <= 123;
UPDATE `sentences` SET `title` = '{"t0":"剪头发", "t1":"剪发", "t2":"染发"}' WHERE `id` >= 124 and `id` <= 127;
UPDATE `sentences` SET `title` = '{"t0":"剪头发", "t1":"评价", "t2":"NULL"}' WHERE `id` >= 128 and `id` <= 130;
select * from sentences where module_id=8;
UPDATE `sentences` SET `title` = '{"t0":"看医生", "t1":"预约", "t2":"挂号"}' WHERE `id` >= 221 and `id` <= 224;
UPDATE `sentences` SET `title` = '{"t0":"看医生", "t1":"预约", "t2":"就诊"}' WHERE `id` >= 225 and `id` <= 227;
UPDATE `sentences` SET `title` = '{"t0":"看医生", "t1":"药店", "t2":"购买药品"}' WHERE `id` >= 228 and `id` <= 231;
UPDATE `sentences` SET `title` = '{"t0":"看医生", "t1":"药店", "t2":"咨询药剂师"}' WHERE `id` >= 232 and `id` <= 234;
select * from sentences where module_id=9;
UPDATE `sentences` SET `title` = '{"t0":"交通", "t1":"公共交通", "t2":"NULL"}' WHERE `id` >= 131 and `id` <= 137;
UPDATE `sentences` SET `title` = '{"t0":"交通", "t1":"叫车", "t2":"NULL"}' WHERE `id` >= 138 and `id` <= 142;
UPDATE `sentences` SET `title` = '{"t0":"交通", "t1":"自驾", "t2":"NULL"}' WHERE `id` >= 143 and `id` <= 149;
-- ==========================================
-- 3. 学习记录与词书 (User Learning Data)
-- 对应图中：生词本、错题本、自定义词书
-- ==========================================

-- 生词本 (New Words)
-- 排序：按添加时间
CREATE TABLE `user_unknown` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `word_id` BIGINT UNSIGNED,
  `sentence_id` BIGINT UNSIGNED,
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
  `word_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '如果是单词',
  `sentence_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '如果是句子',
  `module_id` BIGINT UNSIGNED NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='错题本(含遗忘曲线算法字段)';

-- 自定义词书 (Custom Book)
CREATE TABLE `user_custom_books` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
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

CREATE TABLE `user_listen_progress` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `module_id` BIGINT UNSIGNED NOT NULL ,
  `type` VARCHAR(100) NOT NULL COMMENT 'word/sentence/custom_word/custom_sentence',
  `curr_index` BIGINT UNSIGNED NOT NULL ,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
