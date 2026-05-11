-- 若你使用的是旧版 schema（无收货信息字段），在 MySQL 中执行：
USE campus_trade;
ALTER TABLE tb_order ADD COLUMN address VARCHAR(500) NULL COMMENT '收货地址' AFTER status;
ALTER TABLE tb_order ADD COLUMN contact_phone VARCHAR(32) NULL COMMENT '联系电话' AFTER address;
ALTER TABLE tb_order ADD COLUMN remark VARCHAR(500) NULL COMMENT '备注' AFTER contact_phone;
ALTER TABLE tb_order ADD COLUMN pay_method VARCHAR(32) NULL COMMENT '支付方式' AFTER remark;
