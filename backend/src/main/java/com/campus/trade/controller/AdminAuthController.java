package com.campus.trade.controller;

import com.campus.trade.common.JwtUtil;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Admin;
import com.campus.trade.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/admin/login")
    public Result adminLogin(@RequestBody Admin body) {
        if (body == null || !StringUtils.hasText(body.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (!StringUtils.hasText(body.getPassword())) {
            return Result.error("密码不能为空");
        }
        Admin admin = adminService.findByUsername(body.getUsername());
        if (admin == null) {
            return Result.error("管理员账号不存在");
        }
        if (!passwordEncoder.matches(body.getPassword(), admin.getPassword())) {
            return Result.error("密码错误");
        }
        String token = jwtUtil.createToken(admin.getId(), "ADMIN");
        Map<String, Object> data = new HashMap<>(4);
        data.put("token", token);
        admin.setPassword(null);
        data.put("adminInfo", admin);
        return Result.success(data);
    }
}
