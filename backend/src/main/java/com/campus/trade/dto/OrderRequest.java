package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 订单创建请求（对应论文 5.1.5）
 */
@Data
public class OrderRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    private Integer quantity;
    /** 以下字段与结算界面一致，可选 */
    private String address;
    private String contactPhone;
    private String remark;
    private String payMethod;
}
