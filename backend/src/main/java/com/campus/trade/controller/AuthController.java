package com.campus.trade.controller;

import com.campus.trade.common.JwtUtil;
import com.campus.trade.common.Result;
import com.campus.trade.dto.RegisterRequest;
import com.campus.trade.entity.User;
import com.campus.trade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户注册 / 登录（论文 5.1.1 Controller 层）
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword(),
                request.getStudentId(), request.getPhone());
        return Result.success("注册成功，请登录");
    }

    /**
     * 用户登录接口 —— 结构与论文 5.1.1 保持一致：BCrypt 校验 + JWT。
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if (user == null || !StringUtils.hasText(user.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            return Result.error("密码不能为空");
        }

        User loginUser = userService.findByName(user.getUsername());

        if (loginUser == null) {
            return Result.error("用户名不存在");
        }

        if (!passwordEncoder.matches(user.getPassword(), loginUser.getPassword())) {
            return Result.error("密码错误");
        }

        if (loginUser.getStatus() != null && loginUser.getStatus() == 1) {
            return Result.error("账号已被禁用");
        }

        String token = jwtUtil.createToken(loginUser.getId(), "USER");

        Map<String, Object> data = new HashMap<>(4);
        data.put("token", token);
        data.put("userInfo", userService.safe(loginUser));
        return Result.success(data);
    }
}
