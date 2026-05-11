package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CheckoutRequest {
    @NotEmpty(message = "请选择要结算的购物车条目")
    private List<Long> cartIds;

    private String address;
    private String contactPhone;
    private String remark;
    private String payMethod;
}
