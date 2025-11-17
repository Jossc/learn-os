-- =============================================
-- 支付中台 - 数据库配置表设计
-- =============================================

-- 1. 渠道配置表
CREATE TABLE `channel_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_code` VARCHAR(50) NOT NULL COMMENT '渠道编码: taiping, ailianjk, h5pay',
  `channel_name` VARCHAR(100) NOT NULL COMMENT '渠道名称',
  `strategy_class` VARCHAR(255) NOT NULL COMMENT '策略类全限定名',
  `priority` INT(11) NOT NULL DEFAULT 0 COMMENT '优先级，数字越大优先级越高',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `config_json` TEXT COMMENT '渠道配置JSON: 如商户号、密钥等',
  `description` VARCHAR(500) COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` VARCHAR(50) COMMENT '创建人',
  `update_by` VARCHAR(50) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_code` (`channel_code`),
  KEY `idx_status_priority` (`status`, `priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付渠道配置表';

-- 2. 支付方式配置表
CREATE TABLE `payment_method_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `method_code` VARCHAR(50) NOT NULL COMMENT '支付方式编码: WX_LITE, WX_H5, ALIPAY_H5',
  `method_name` VARCHAR(100) NOT NULL COMMENT '支付方式名称',
  `channel_code` VARCHAR(50) NOT NULL COMMENT '所属渠道编码',
  `strategy_class` VARCHAR(255) NOT NULL COMMENT '策略类全限定名',
  `priority` INT(11) NOT NULL DEFAULT 0 COMMENT '优先级',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `config_json` TEXT COMMENT '支付方式配置JSON',
  `description` VARCHAR(500) COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_method_code` (`method_code`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_status_priority` (`status`, `priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付方式配置表';

-- 3. 路由规则配置表
CREATE TABLE `route_rule_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `rule_type` VARCHAR(50) NOT NULL COMMENT '规则类型: SUCCESS_RATE, COST_OPTIMIZE, LIMIT, REGION',
  `condition_json` TEXT NOT NULL COMMENT '路由条件JSON: {amount_min, amount_max, region, user_level}',
  `target_channel` VARCHAR(50) NOT NULL COMMENT '目标渠道',
  `target_method` VARCHAR(50) COMMENT '目标支付方式',
  `priority` INT(11) NOT NULL DEFAULT 0 COMMENT '优先级',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `weight` INT(11) DEFAULT 100 COMMENT '权重(用于A/B测试)',
  `description` VARCHAR(500) COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_status_priority` (`status`, `priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付路由规则配置表';

-- 4. 风控规则配置表
CREATE TABLE `risk_rule_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_code` VARCHAR(50) NOT NULL COMMENT '规则编码',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `rule_type` VARCHAR(50) NOT NULL COMMENT '规则类型: AMOUNT, FREQUENCY, BLACKLIST, IP, DEVICE',
  `rule_expression` TEXT NOT NULL COMMENT '规则表达式(Drools或Groovy)',
  `risk_level` VARCHAR(20) NOT NULL COMMENT '风险等级: LOW, MEDIUM, HIGH',
  `action` VARCHAR(50) NOT NULL COMMENT '处理动作: PASS, REJECT, VERIFY',
  `priority` INT(11) NOT NULL DEFAULT 0 COMMENT '优先级',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `description` VARCHAR(500) COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_code` (`rule_code`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_status_priority` (`status`, `priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风控规则配置表';

-- 5. 限额配置表
CREATE TABLE `limit_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `limit_type` VARCHAR(50) NOT NULL COMMENT '限额类型: USER_SINGLE, USER_DAY, USER_MONTH, CHANNEL_DAY',
  `channel_code` VARCHAR(50) COMMENT '渠道编码',
  `method_code` VARCHAR(50) COMMENT '支付方式编码',
  `limit_amount` DECIMAL(15,2) NOT NULL COMMENT '限额金额',
  `user_level` VARCHAR(50) COMMENT '用户等级: NORMAL, VIP, SVIP',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `description` VARCHAR(500) COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_channel_method` (`channel_code`, `method_code`),
  KEY `idx_limit_type` (`limit_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付限额配置表';

-- 6. 渠道健康状态表 (实时更新)
CREATE TABLE `channel_health_status` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_code` VARCHAR(50) NOT NULL COMMENT '渠道编码',
  `method_code` VARCHAR(50) COMMENT '支付方式编码',
  `success_rate` DECIMAL(5,2) NOT NULL COMMENT '成功率 (%)',
  `avg_response_time` INT(11) NOT NULL COMMENT '平均响应时间 (ms)',
  `total_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '总交易笔数',
  `success_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '成功笔数',
  `fail_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '失败笔数',
  `health_score` INT(11) NOT NULL DEFAULT 100 COMMENT '健康分数 (0-100)',
  `is_available` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否可用: 0-不可用, 1-可用',
  `last_check_time` DATETIME NOT NULL COMMENT '最后检查时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_method` (`channel_code`, `method_code`),
  KEY `idx_is_available` (`is_available`),
  KEY `idx_health_score` (`health_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道健康状态表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入渠道配置
INSERT INTO `channel_config` (`channel_code`, `channel_name`, `strategy_class`, `priority`, `status`, `description`) VALUES
('taiping', '太平保险渠道', 'com.example.payment.strategy.channel.TaipingChannelStrategy', 100, 1, '太平保险专属支付渠道'),
('ailianjk', '爱联健康渠道', 'com.example.payment.strategy.channel.AilianjkChannelStrategy', 90, 1, '爱联健康专属支付渠道'),
('h5pay', 'H5通用渠道', 'com.example.payment.strategy.channel.H5ChannelStrategy', 80, 1, 'H5通用支付渠道');

-- 插入支付方式配置
INSERT INTO `payment_method_config` (`method_code`, `method_name`, `channel_code`, `strategy_class`, `priority`, `status`, `description`) VALUES
('WX_LITE', '微信小程序支付', 'taiping', 'com.example.payment.strategy.method.WechatLitePayStrategy', 100, 1, '微信小程序JSAPI支付'),
('WX_H5', '微信H5支付', 'taiping', 'com.example.payment.strategy.method.WechatH5PayStrategy', 90, 1, '微信H5网页支付'),
('ALIPAY_H5', '支付宝H5支付', 'taiping', 'com.example.payment.strategy.method.AlipayH5PayStrategy', 85, 1, '支付宝H5网页支付'),
('ALIPAY_APP', '支付宝APP支付', 'taiping', 'com.example.payment.strategy.method.AlipayAppPayStrategy', 80, 1, '支付宝APP原生支付');

-- 插入路由规则配置
INSERT INTO `route_rule_config` (`rule_name`, `rule_type`, `condition_json`, `target_channel`, `target_method`, `priority`, `status`, `description`) VALUES
('小额订单路由', 'COST_OPTIMIZE', '{"amount_min": 0, "amount_max": 100}', 'taiping', 'WX_LITE', 100, 1, '小额订单优先使用成本较低的微信支付'),
('大额订单路由', 'SUCCESS_RATE', '{"amount_min": 1000, "amount_max": 999999}', 'taiping', 'ALIPAY_H5', 90, 1, '大额订单优先使用成功率高的支付宝'),
('地域路由-北京', 'REGION', '{"region": "北京"}', 'taiping', 'WX_LITE', 80, 1, '北京地区优先使用微信支付');

-- 插入风控规则配置
INSERT INTO `risk_rule_config` (`rule_code`, `rule_name`, `rule_type`, `rule_expression`, `risk_level`, `action`, `priority`, `status`, `description`) VALUES
('AMOUNT_LIMIT', '单笔金额限制', 'AMOUNT', 'amount > 5000', 'HIGH', 'VERIFY', 100, 1, '单笔金额超过5000需要验证'),
('FREQUENCY_LIMIT', '交易频率限制', 'FREQUENCY', 'count_in_1hour > 10', 'MEDIUM', 'REJECT', 90, 1, '1小时内交易超过10笔拒绝'),
('BLACKLIST_CHECK', '黑名单检查', 'BLACKLIST', 'in_blacklist == true', 'HIGH', 'REJECT', 95, 1, '黑名单用户直接拒绝');

-- 插入限额配置
INSERT INTO `limit_config` (`limit_type`, `channel_code`, `method_code`, `limit_amount`, `user_level`, `status`, `description`) VALUES
('USER_SINGLE', 'taiping', 'WX_LITE', 5000.00, 'NORMAL', 1, '普通用户单笔限额'),
('USER_SINGLE', 'taiping', 'WX_LITE', 20000.00, 'VIP', 1, 'VIP用户单笔限额'),
('USER_DAY', 'taiping', NULL, 50000.00, 'NORMAL', 1, '普通用户日累计限额'),
('CHANNEL_DAY', 'taiping', NULL, 10000000.00, NULL, 1, '渠道日累计限额');

-- 初始化渠道健康状态
INSERT INTO `channel_health_status` (`channel_code`, `method_code`, `success_rate`, `avg_response_time`, `total_count`, `success_count`, `fail_count`, `health_score`, `is_available`, `last_check_time`) VALUES
('taiping', 'WX_LITE', 98.50, 350, 10000, 9850, 150, 98, 1, NOW()),
('taiping', 'WX_H5', 97.80, 420, 8000, 7824, 176, 97, 1, NOW()),
('taiping', 'ALIPAY_H5', 99.20, 300, 12000, 11904, 96, 99, 1, NOW()),
('taiping', 'ALIPAY_APP', 98.90, 280, 9000, 8901, 99, 98, 1, NOW());

