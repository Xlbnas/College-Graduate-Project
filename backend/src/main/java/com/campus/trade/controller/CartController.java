package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.service.CartService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result list() {
        rejectAdmin();
        return Result.success(cartService.listByUser(UserContext.getUserId()));
    }

    @PostMapping
    public Result add(@RequestBody CartAddBody body) {
        rejectAdmin();
        cartService.add(UserContext.getUserId(), body.getProductId(), body.getQuantity());
        return Result.success("已加入购物车");
    }

    @DeleteMapping("/{cartId}")
    public Result delete(@PathVariable Long cartId) {
        rejectAdmin();
        cartService.remove(UserContext.getUserId(), cartId);
        return Result.success("已删除");
    }

    @PutMapping("/{cartId}")
    public Result updateQty(@PathVariable Long cartId, @RequestBody CartAddBody body) {
        rejectAdmin();
        cartService.updateQuantity(UserContext.getUserId(), cartId, body.getQuantity() == null ? 1 : body.getQuantity());
        return Result.success("已更新");
    }

    private void rejectAdmin() {
        if (!UserContext.isUser()) {
            throw new BusinessException("学生功能，请使用学生账号");
        }
    }

    @Data
    private static class CartAddBody {
        private Long productId;
        private Integer quantity;
    }
}
