package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商品分类表（论文表 4-5）
 */
@Data
@TableName("tb_category")
public class Category {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;
}
