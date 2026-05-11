package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单信息表（论文表 4-4 + quantity 字段，对应论文示例代码）。
 * status：WAIT_PAY 待支付，PAID 待发货，SHIPPED 待收货，COMPLETED 已完成
 */
@Data
@TableName("tb_order")
public class Order {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    @TableField("product_id")
    private Long productId;

    @TableField("total_price")
    private BigDecimal totalPrice;

    private Integer quantity;

    private String status;

    private String address;

    @TableField("contact_phone")
    private String contactPhone;

    private String remark;

    @TableField("pay_method")
    private String payMethod;

    @TableField("review_content")
    private String reviewContent;

    @TableField("review_time")
    private Date reviewTime;

    @TableField("create_time")
    private Date createTime;

    @TableField(exist = false)
    private String productTitle;

    @TableField(exist = false)
    private String buyerDisplayName;

    @TableField(exist = false)
    private String sellerDisplayName;
}
