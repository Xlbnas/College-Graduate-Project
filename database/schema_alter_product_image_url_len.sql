-- 外链封面图（如 Unsplash）可能超过 255 字符；演示环境建议 512。
USE campus_trade;
ALTER TABLE tb_product MODIFY COLUMN image_url VARCHAR(512) NULL COMMENT '封面图（相对路径或 https 外链）';
