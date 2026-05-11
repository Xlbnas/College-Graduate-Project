package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class OrderReviewRequest {

    @NotBlank(message = "请填写评价内容")
    @Size(max = 500, message = "评价内容不能超过 500 字")
    private String content;
}
