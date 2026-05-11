package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品信息表（论文表 4-3 + 审核扩展字段）。
 * status：PENDING 待审核，ON_SALE 上架，REJECTED 拒绝，OFF_SALE 下架，SOLD_OUT 已售
 */
@Data
@TableName("tb_product")
public class Product {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    @TableField("image_url")
    private String imageUrl;

    @TableField("user_id")
    private Long userId;

    @TableField("category_id")
    private Long categoryId;

    private String status;

    @TableField("create_time")
    private Date createTime;

    @TableField("audit_time")
    private Date auditTime;

    @TableField("audit_result")
    private String auditResult;

    @TableField("reject_reason")
    private String rejectReason;

    /** 非表字段 —— 列表/详情展示用 */
    @TableField(exist = false)
    private String sellerUsername;

    @TableField(exist = false)
    private String categoryName;
}
