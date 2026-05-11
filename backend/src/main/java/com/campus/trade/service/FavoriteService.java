package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.entity.Favorite;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductService productService;

    public List<Favorite> listByUser(Long userId) {
        return favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime));
    }

    public void add(Long userId, Long productId) {
        if (productService.getById(productId) == null) {
            throw new BusinessException("商品不存在");
        }
        Favorite exist = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
        if (exist != null) {
            return;
        }
        Favorite f = new Favorite();
        f.setUserId(userId);
        f.setProductId(productId);
        f.setCreateTime(new Date());
        favoriteMapper.insert(f);
    }

    public void remove(Long userId, Long productId) {
        Favorite exist = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
        if (exist == null || !Objects.equals(exist.getUserId(), userId)) {
            throw new BusinessException("收藏记录不存在");
        }
        favoriteMapper.deleteById(exist.getId());
    }

    public boolean isFavorite(Long userId, Long productId) {
        if (userId == null) {
            return false;
        }
        return favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId)) > 0;
    }
}
