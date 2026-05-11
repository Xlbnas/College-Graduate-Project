package com.campus.trade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.CheckoutRequest;
import com.campus.trade.dto.OrderRequest;
import com.campus.trade.dto.OrderReviewRequest;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.service.OrderService;
import com.campus.trade.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单创建与履约（对应论文 5.1.5）
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;

    /**
     * 创建订单（论文同款：校验商品 + 写入订单 + 更新商品在售状态）
     */
    @PostMapping
    public Result createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        requireStudent();
        Map<String, Object> data = orderService.createOrder(UserContext.getUserId(), orderRequest);
        return Result.success(data);
    }

    @PostMapping("/checkout")
    public Result checkout(@RequestBody @Valid CheckoutRequest checkoutRequest) {
        requireStudent();
        return Result.success(orderService.checkoutCart(UserContext.getUserId(), checkoutRequest));
    }

    @GetMapping
    public Result myOrders(@RequestParam(defaultValue = "1") int pageNum,
                          @RequestParam(defaultValue = "10") int pageSize,
                          @RequestParam(required = false) String status) {
        requireStudent();
        Page<Order> p = orderService.pageMine(UserContext.getUserId(), pageNum, pageSize, status);
        for (Order o : p.getRecords()) {
            Product pr = productService.getById(o.getProductId());
            o.setProductTitle(pr != null ? pr.getTitle() : "商品记录");
        }
        orderService.fillOrderParties(p.getRecords());
        Map<String, Object> map = new HashMap<>(8);
        map.put("records", p.getRecords());
        map.put("total", p.getTotal());
        map.put("pageNum", p.getCurrent());
        map.put("pageSize", p.getSize());
        return Result.success(map);
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        requireStudent();
        Order o = orderService.getById(id);
        if (o == null || !java.util.Objects.equals(o.getUserId(), UserContext.getUserId())) {
            return Result.error("订单不存在或无权限查看");
        }
        return Result.success(o);
    }

    @PostMapping("/{id}/pay")
    public Result pay(@PathVariable Long id) {
        requireStudent();
        orderService.pay(id, UserContext.getUserId());
        return Result.success("支付成功（模拟）");
    }

    @PostMapping("/{id}/ship")
    public Result ship(@PathVariable Long id) {
        if (!UserContext.isUser() && !UserContext.isAdmin()) {
            return Result.error("请登录后再操作");
        }
        boolean admin = UserContext.isAdmin();
        orderService.ship(id, UserContext.getUserId(), admin);
        return Result.success("已发货（模拟）");
    }

    @PostMapping("/{id}/receive")
    public Result receive(@PathVariable Long id) {
        requireStudent();
        orderService.receive(id, UserContext.getUserId());
        return Result.success("已确认收货，交易完成");
    }

    @PostMapping("/{id}/review")
    public Result review(@PathVariable Long id, @RequestBody @Valid OrderReviewRequest body) {
        requireStudent();
        orderService.review(id, UserContext.getUserId(), body.getContent());
        return Result.success("评价已提交");
    }

    private void requireStudent() {
        if (!UserContext.isUser()) {
            throw new BusinessException("请使用学生账号操作订单");
        }
    }
}
