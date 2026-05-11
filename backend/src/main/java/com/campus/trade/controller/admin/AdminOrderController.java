package com.campus.trade.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Order;
import com.campus.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public Result list(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "12") int pageSize) {
        Page<Order> p = orderService.pageAdmin(pageNum, pageSize);
        Map<String, Object> map = new HashMap<>(8);
        map.put("records", p.getRecords());
        map.put("total", p.getTotal());
        map.put("pageNum", p.getCurrent());
        map.put("pageSize", p.getSize());
        return Result.success(map);
    }
}
