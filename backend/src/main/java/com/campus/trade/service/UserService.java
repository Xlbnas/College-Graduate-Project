package com.campus.trade.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.entity.User;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 批量查询卖家展示名：优先 real_name，空则回退 username。
     */
    public Map<Long, String> usernameMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(User::getId, this::displayName, (a, b) -> a));
    }

    private String displayName(User u) {
        if (StrUtil.isNotBlank(u.getRealName())) {
            return u.getRealName().trim();
        }
        return u.getUsername() != null ? u.getUsername() : "";
    }

    public User findByName(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    public User register(String username, String password, String studentId, String phone) {
        validateRegister(username, password, studentId, phone);
        if (findByName(username) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, studentId)) != null) {
            throw new BusinessException("学号已被注册");
        }
        if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) != null) {
            throw new BusinessException("手机号已被绑定");
        }
        User u = new User();
        u.setUsername(username.trim());
        u.setRealName("");
        u.setPassword(passwordEncoder.encode(password));
        u.setStudentId(studentId.trim());
        u.setPhone(phone.trim());
        u.setRole("USER");
        u.setStatus(0);
        u.setCreateTime(new Date());
        userMapper.insert(u);
        return u;
    }

    private void validateRegister(String username, String password, String studentId, String phone) {
        if (StrUtil.isBlank(username)) {
            throw new BusinessException("用户名不能为空");
        }
        if (StrUtil.isBlank(password)) {
            throw new BusinessException("密码不能为空");
        }
        if (StrUtil.isBlank(studentId)) {
            throw new BusinessException("学号不能为空（实名认证）");
        }
        if (StrUtil.isBlank(phone)) {
            throw new BusinessException("手机号不能为空（实名认证）");
        }
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    public void setDisabled(Long userId, int status) {
        User u = userMapper.selectById(userId);
        if (u == null) {
            throw new BusinessException("用户不存在");
        }
        u.setStatus(status);
        userMapper.updateById(u);
    }

    /**
     * 清除密码等信息再返回前端
     */
    public User safe(User u) {
        if (u == null) {
            return null;
        }
        u.setPassword(null);
        return u;
    }
}
