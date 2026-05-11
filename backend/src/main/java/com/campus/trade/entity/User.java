package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息表（论文表 4-2），学生用户 role 固定 USER；status：0正常 1禁用
 */
@Data
@TableName("tb_user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    @TableField("real_name")
    private String realName;

    /** 可接收登录 JSON 中的 password；序列化到前端时不输出 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @TableField("student_id")
    private String studentId;

    private String phone;

    private String role;

    /**
     * 0：正常；1：禁用（论文登录校验）
     */
    private Integer status;

    @TableField("create_time")
    private Date createTime;
}
