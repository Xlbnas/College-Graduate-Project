package com.campus.trade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.service.OrderService;
import com.campus.trade.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 学生个人中心：我的发布 / 我的售出订单
 */
@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyCenterController {

    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping("/products")
    public Result myPublish() {
        if (!UserContext.isUser()) {
            return Result.error("请使用学生账号访问");
        }
        return Result.success(productService.listBySeller(UserContext.getUserId()));
    }

    @GetMapping("/sales-orders")
    public Result salesOrders(@RequestParam(defaultValue = "1") int pageNum,
                              @RequestParam(defaultValue = "10") int pageSize,
                              @RequestParam(required = false) String status) {
        if (!UserContext.isUser()) {
            return Result.error("请使用学生账号访问");
        }
        Page<Order> p = orderService.pageForSeller(UserContext.getUserId(), pageNum, pageSize, status);
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
}
