package com.campus.trade.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.entity.Product;
import com.campus.trade.service.NotificationService;
import com.campus.trade.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 后台商品管理与审核（论文 5.1.3 商品审核）
 */
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final NotificationService notificationService;

    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) String status) {
        Page<Product> pg = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>().orderByDesc(Product::getCreateTime);
        if (status != null && !status.isEmpty()) {
            qw.eq(Product::getStatus, status);
        }
        IPage<Product> res = productService.pageQuery(pg, qw);
        Map<String, Object> map = new HashMap<>(8);
        map.put("records", res.getRecords());
        map.put("total", res.getTotal());
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        return Result.success(map);
    }

    /**
     * 商品审核（论文同款接口）：POST /admin/products/audit
     */
    @PostMapping("/audit")
    public Result auditProduct(@RequestParam Long productId,
                               @RequestParam String auditStatus,
                               @RequestParam(required = false) String reason) {
        if (!UserContext.isAdmin()) {
            return Result.error("权限不足");
        }
        Product product = productService.getById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        if ("PASS".equalsIgnoreCase(auditStatus)) {
            product.setStatus("ON_SALE");
            product.setAuditTime(new Date());
            product.setAuditResult("通过");
            product.setRejectReason(null);
        } else if ("REJECT".equalsIgnoreCase(auditStatus)) {
            product.setStatus("REJECTED");
            product.setAuditTime(new Date());
            product.setAuditResult("拒绝");
            product.setRejectReason(reason);
            notificationService.sendNotify(product.getUserId(),
                    "您的商品《" + product.getTitle() + "》审核未通过，原因：" + (reason == null ? "无" : reason));
        } else {
            return Result.error("审核状态不正确");
        }
        boolean ok = productService.update(product);
        if (ok) {
            return Result.success("审核完成");
        }
        return Result.error("审核失败");
    }

    @PostMapping("/{id}/off-sale")
    public Result offSale(@PathVariable Long id) {
        productService.offSale(id);
        return Result.success("已强制下架");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        productService.deleteById(id);
        return Result.success("已删除");
    }
}
