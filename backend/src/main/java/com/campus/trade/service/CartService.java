package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.entity.Cart;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final ProductService productService;

    public List<Cart> listByUser(Long userId) {
        return cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime));
    }

    public void add(Long userId, Long productId, Integer quantity) {
        Product p = productService.getById(productId);
        if (p == null) {
            throw new BusinessException("商品不存在");
        }
        if (!"ON_SALE".equals(p.getStatus())) {
            throw new BusinessException("仅可加入上架中的商品");
        }
        if (Objects.equals(p.getUserId(), userId)) {
            throw new BusinessException("不能将本人发布的商品加入购物车");
        }
        int q = quantity == null || quantity < 1 ? 1 : quantity;
        Cart exist = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId));
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + q);
            cartMapper.updateById(exist);
        } else {
            Cart c = new Cart();
            c.setUserId(userId);
            c.setProductId(productId);
            c.setQuantity(q);
            c.setCreateTime(new Date());
            cartMapper.insert(c);
        }
    }

    public void remove(Long userId, Long cartId) {
        Cart c = cartMapper.selectById(cartId);
        if (c == null || !Objects.equals(c.getUserId(), userId)) {
            throw new BusinessException("无权删除");
        }
        cartMapper.deleteById(cartId);
    }

    public void updateQuantity(Long userId, Long cartId, int quantity) {
        if (quantity < 1) {
            throw new BusinessException("数量至少为1");
        }
        Cart c = cartMapper.selectById(cartId);
        if (c == null || !Objects.equals(c.getUserId(), userId)) {
            throw new BusinessException("无权修改");
        }
        c.setQuantity(quantity);
        cartMapper.updateById(c);
    }
}
