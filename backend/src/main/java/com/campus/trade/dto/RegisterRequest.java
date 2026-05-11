package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "学号不能为空")
    private String studentId;

    @NotBlank(message = "手机号不能为空")
    private String phone;
}
