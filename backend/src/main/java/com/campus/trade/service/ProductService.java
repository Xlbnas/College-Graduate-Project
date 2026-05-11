package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final UserService userService;
    private final CategoryService categoryService;

    public boolean addProduct(Product product) {
        return productMapper.insert(product) > 0;
    }

    public boolean update(Product product) {
        return productMapper.updateById(product) > 0;
    }

    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    /** 填充卖家昵称、分类名称（非持久化字段） */
    public void fillDisplayFields(List<Product> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Set<Long> uids = list.stream().map(Product::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> cids = list.stream().map(Product::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        java.util.Map<Long, String> users = userService.usernameMap(uids);
        java.util.Map<Long, String> cats = categoryService.nameMap(cids);
        for (Product p : list) {
            p.setSellerUsername(users.get(p.getUserId()));
            p.setCategoryName(cats.get(p.getCategoryId()));
        }
    }

    public void fillDisplayFields(Product p) {
        if (p == null) {
            return;
        }
        fillDisplayFields(java.util.Collections.singletonList(p));
    }

    public IPage<Product> pageQuery(Page<Product> page, LambdaQueryWrapper<Product> wrapper) {
        return productMapper.selectPage(page, wrapper);
    }

    public List<Product> listBySeller(Long sellerId) {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getUserId, sellerId)
                .orderByDesc(Product::getCreateTime));
    }

    public List<Long> listProductIdsBySeller(Long sellerId) {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                        .eq(Product::getUserId, sellerId)
                        .select(Product::getId))
                .stream()
                .map(Product::getId)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        Product p = getById(id);
        if (p == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.deleteById(id);
    }

    public void offSale(Long id) {
        Product p = getById(id);
        if (p == null) {
            throw new BusinessException("商品不存在");
        }
        if ("SOLD_OUT".equals(p.getStatus())) {
            throw new BusinessException("已售出商品无法下架");
        }
        p.setStatus("OFF_SALE");
        update(p);
    }
}
