-- 已有库：订单买家评价（完成后一次，买卖双方订单页可见）
USE campus_trade;

ALTER TABLE tb_order ADD COLUMN review_content VARCHAR(500) NULL COMMENT '买家评价' AFTER pay_method;
ALTER TABLE tb_order ADD COLUMN review_time DATETIME NULL COMMENT '评价时间' AFTER review_content;
