package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.dto.CheckoutRequest;
import com.campus.trade.dto.OrderRequest;
import com.campus.trade.entity.Cart;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.CartMapper;
import com.campus.trade.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单交易（对应论文 5.1.5：事务 + 扣减上架状态）。
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final ProductService productService;
    private final UserService userService;

    /** 卖家查看涉及自己商品的订单 */
    public Page<Order> pageForSeller(Long sellerId, int pageNum, int pageSize, String status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        List<Long> saleIds = productService.listProductIdsBySeller(sellerId);
        if (saleIds.isEmpty()) {
            page.setRecords(Collections.emptyList());
            page.setTotal(0);
            return page;
        }
        LambdaQueryWrapper<Order> w = new LambdaQueryWrapper<Order>()
                .in(Order::getProductId, saleIds);
        if (status != null && !status.trim().isEmpty()) {
            w.eq(Order::getStatus, status);
        }
        w.orderByDesc(Order::getCreateTime);
        orderMapper.selectPage(page, w);
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createOrder(Long buyerId, OrderRequest orderRequest) {
        Product product = productService.getById(orderRequest.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!"ON_SALE".equals(product.getStatus())) {
            throw new BusinessException("商品已下架或已售出");
        }
        if (Objects.equals(product.getUserId(), buyerId)) {
            throw new BusinessException("不能购买自己发布的商品");
        }
        int quantity = orderRequest.getQuantity() == null ? 1 : orderRequest.getQuantity();
        if (quantity < 1) {
            throw new BusinessException("购买数量无效");
        }

        String orderNo = "ORD" + System.currentTimeMillis() + new Random().nextInt(1000);

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(buyerId);
        order.setProductId(product.getId());
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        order.setStatus("WAIT_PAY");
        order.setCreateTime(new Date());
        order.setAddress(orderRequest.getAddress());
        order.setContactPhone(orderRequest.getContactPhone());
        order.setRemark(orderRequest.getRemark());
        order.setPayMethod(orderRequest.getPayMethod());

        if (orderMapper.insert(order) <= 0) {
            throw new BusinessException("订单创建失败");
        }

        product.setStatus("SOLD_OUT");
        if (!productService.update(product)) {
            throw new BusinessException("商品状态更新失败");
        }

        Map<String, Object> data = new HashMap<>(4);
        data.put("orderId", order.getId());
        data.put("orderNo", order.getOrderNo());
        data.put("totalPrice", order.getTotalPrice());
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> checkoutCart(Long buyerId, CheckoutRequest checkout) {
        List<Long> cartIds = checkout.getCartIds();
        if (cartIds == null || cartIds.isEmpty()) {
            throw new BusinessException("请选择结算商品");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Long cartId : cartIds) {
            Cart cart = cartMapper.selectById(cartId);
            if (cart == null || !Objects.equals(cart.getUserId(), buyerId)) {
                throw new BusinessException("购物车条目无效");
            }
            OrderRequest req = new OrderRequest();
            req.setProductId(cart.getProductId());
            req.setQuantity(1);
            req.setAddress(checkout.getAddress());
            req.setContactPhone(checkout.getContactPhone());
            req.setRemark(checkout.getRemark());
            req.setPayMethod(checkout.getPayMethod());
            result.add(createOrder(buyerId, req));
            cartMapper.deleteById(cartId);
        }
        return result;
    }

    public Page<Order> pageMine(Long userId, int pageNum, int pageSize, String status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> w = new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            w.eq(Order::getStatus, status);
        }
        w.orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(page, w);
    }

    public Page<Order> pageAdmin(int pageNum, int pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        return orderMapper.selectPage(page, new LambdaQueryWrapper<Order>().orderByDesc(Order::getCreateTime));
    }

    public Order getById(Long id) {
        return orderMapper.selectById(id);
    }

    /** 填充买卖双方展示名（优先 real_name，空则 username） */
    public void fillOrderParties(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        Set<Long> buyerIds = orders.stream()
                .map(Order::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> buyerNames = userService.usernameMap(buyerIds);

        Set<Long> productIds = orders.stream()
                .map(Order::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Long> productSeller = new HashMap<>(productIds.size());
        for (Long productId : productIds) {
            Product product = productService.getById(productId);
            if (product != null && product.getUserId() != null) {
                productSeller.put(productId, product.getUserId());
            }
        }
        Map<Long, String> sellerNames = userService.usernameMap(productSeller.values());

        for (Order order : orders) {
            if (order.getUserId() != null) {
                order.setBuyerDisplayName(buyerNames.get(order.getUserId()));
            }
            Long sellerId = productSeller.get(order.getProductId());
            if (sellerId != null) {
                order.setSellerDisplayName(sellerNames.get(sellerId));
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void pay(Long orderId, Long buyerId) {
        Order order = requireOrder(orderId);
        if (!Objects.equals(order.getUserId(), buyerId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (!"WAIT_PAY".equals(order.getStatus())) {
            throw new BusinessException("订单状态不可支付");
        }
        order.setStatus("PAID");
        orderMapper.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void ship(Long orderId, Long operatorId, boolean admin) {
        Order order = requireOrder(orderId);
        Product product = productService.getById(order.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        boolean seller = Objects.equals(product.getUserId(), operatorId);
        if (!admin && !seller) {
            throw new BusinessException("仅卖家或管理员可发货");
        }
        if (!"PAID".equals(order.getStatus())) {
            throw new BusinessException("订单状态不可发货");
        }
        order.setStatus("SHIPPED");
        orderMapper.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void receive(Long orderId, Long buyerId) {
        Order order = requireOrder(orderId);
        if (!Objects.equals(order.getUserId(), buyerId)) {
            throw new BusinessException("仅买家可确认收货");
        }
        if (!"SHIPPED".equals(order.getStatus())) {
            throw new BusinessException("订单状态不可确认收货");
        }
        order.setStatus("COMPLETED");
        orderMapper.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void review(Long orderId, Long buyerId, String content) {
        Order order = requireOrder(orderId);
        if (!Objects.equals(order.getUserId(), buyerId)) {
            throw new BusinessException("仅买家可评价该订单");
        }
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new BusinessException("订单完成后才能评价");
        }
        if (order.getReviewContent() != null && !order.getReviewContent().trim().isEmpty()) {
            throw new BusinessException("该订单已评价");
        }
        String text = content == null ? "" : content.trim();
        if (text.isEmpty()) {
            throw new BusinessException("请填写评价内容");
        }
        if (text.length() > 500) {
            throw new BusinessException("评价内容不能超过 500 字");
        }
        order.setReviewContent(text);
        order.setReviewTime(new Date());
        orderMapper.updateById(order);
    }

    private Order requireOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }
}
