package com.campus.trade.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize) {
        Page<User> p = userMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime));
        List<User> sanitized = p.getRecords().stream()
                .map(userService::safe)
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>(8);
        map.put("records", sanitized);
        map.put("total", p.getTotal());
        map.put("pageNum", p.getCurrent());
        map.put("pageSize", p.getSize());
        return Result.success(map);
    }

    @PostMapping("/{id}/disable")
    public Result disable(@PathVariable Long id) {
        userService.setDisabled(id, 1);
        return Result.success("已禁用");
    }

    @PostMapping("/{id}/enable")
    public Result enable(@PathVariable Long id) {
        userService.setDisabled(id, 0);
        return Result.success("已解禁");
    }
}
