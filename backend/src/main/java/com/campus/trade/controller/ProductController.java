package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.entity.Product;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.ProductService;
import com.campus.trade.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商品浏览、检索、发布（论文 5.1.2 ~ 5.1.4）
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductMapper productMapper;
    private final ProductService productService;
    private final FileUploadUtil fileUploadUtil;

    /**
     * 商品搜索接口（论文 5.1.4，MyBatis-Plus QueryWrapper + 分页）
     */
    @GetMapping("/search")
    public Result searchProducts(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Long categoryId,
                                @RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ON_SALE");

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            queryWrapper.and(wrapper -> wrapper.like("title", kw).or().like("description", kw));
        }

        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }

        queryWrapper.orderByDesc("create_time");

        Page<Product> page = new Page<>(pageNum, pageSize);
        Page<Product> resultPage = productMapper.selectPage(page, queryWrapper);
        productService.fillDisplayFields(resultPage.getRecords());

        Map<String, Object> data = new HashMap<>(8);
        data.put("list", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }

    /**
     * 首页侧边栏「最新发布」
     */
    @GetMapping("/latest")
    public Result latestOnSale(@RequestParam(defaultValue = "3") Integer limit) {
        int n = Math.max(1, Math.min(limit == null ? 3 : limit, 30));
        QueryWrapper<Product> qw = new QueryWrapper<>();
        qw.eq("status", "ON_SALE").orderByDesc("create_time").last("LIMIT " + n);
        List<Product> list = productMapper.selectList(qw);
        productService.fillDisplayFields(list);
        return Result.success(list);
    }

    @GetMapping("/{id:\\d+}")
    public Result detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        if ("ON_SALE".equals(product.getStatus())) {
            productService.fillDisplayFields(product);
            return Result.success(product);
        }
        Long uid = UserContext.getUserId();
        if (uid != null && (UserContext.isAdmin()
                || Objects.equals(product.getUserId(), uid))) {
            productService.fillDisplayFields(product);
            return Result.success(product);
        }
        return Result.error("商品当前不可见");
    }

    /**
     * 商品发布（论文核心流程 —— multipart 表单模拟论文中的校验与入库逻辑）
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result addProduct(@RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam BigDecimal price,
                             @RequestParam Long categoryId,
                             @RequestPart("image") MultipartFile file) throws Exception {
        if (!UserContext.isUser()) {
            return Result.error("请使用学生账号发布商品");
        }
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description == null ? "" : description);

        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            return Result.error("商品标题不能为空");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("商品价格必须大于0");
        }
        if (file == null || file.isEmpty()) {
            return Result.error("请上传商品图片");
        }

        String imageUrl = fileUploadUtil.upload(file);
        product.setImageUrl(imageUrl);
        product.setPrice(price);
        product.setCategoryId(categoryId);

        product.setStatus("PENDING");
        product.setCreateTime(new Date());
        Long userId = UserContext.getUserId();
        product.setUserId(userId);

        boolean ok = productService.addProduct(product);
        if (ok) {
            return Result.success("发布成功，等待管理员审核");
        }
        return Result.error("发布失败");
    }

}
