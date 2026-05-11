-- 已有库仅更新演示图路径（需已存在 frontend/public/demo 下静态资源）
-- 若 image_url 全为空（首页占位图），可用 scripts/dev-start.sh 里 ensure_product_demo_images 的按分类补全逻辑，或执行后文按 id 的 UPDATE。
USE campus_trade;

UPDATE tb_product SET image_url = '/demo/item-1.svg' WHERE id IN (1,2,3,4);
UPDATE tb_product SET image_url = '/demo/item-2.svg' WHERE id IN (5,6,7,8);
UPDATE tb_product SET image_url = '/demo/item-3.svg' WHERE id IN (9,10,11,12);
UPDATE tb_product SET image_url = '/demo/item-4.svg' WHERE id IN (13,14,15);
UPDATE tb_product SET image_url = '/demo/item-5.svg' WHERE id IN (16,17,18);
UPDATE tb_product SET image_url = '/demo/item-6.svg' WHERE id IN (19,20,21,22);
