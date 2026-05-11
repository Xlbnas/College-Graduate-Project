package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public Result list() {
        requireStudent();
        return Result.success(favoriteService.listByUser(UserContext.getUserId()));
    }

    @PostMapping("/{productId}")
    public Result add(@PathVariable Long productId) {
        requireStudent();
        favoriteService.add(UserContext.getUserId(), productId);
        return Result.success("收藏成功");
    }

    @DeleteMapping("/{productId}")
    public Result remove(@PathVariable Long productId) {
        requireStudent();
        favoriteService.remove(UserContext.getUserId(), productId);
        return Result.success("已取消收藏");
    }

    private void requireStudent() {
        if (!UserContext.isUser()) {
            throw new BusinessException("请使用学生账号");
        }
    }
}
