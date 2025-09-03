package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.enums.PaymentStatus;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto dto) {
        // 1. Load cart for user
        Cart cart = cartRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + dto.getUserId()));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty, cannot create order.");
        }

        // 2. Create order from cart
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setShippingAddress(dto.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice()); // take unit price from Product
            orderItem.setTotalPrice(cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            totalAmount = totalAmount.add(orderItem.getTotalPrice());

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // 3. Save order
        Order savedOrder = orderRepository.save(order);

        // 4. Clear cart after checkout
        cart.getItems().clear();
        cart.setTotalItems(0);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        return orderMapper.toDto(savedOrder);
    }


    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getAllOrders(String filter, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = null;

        switch (filter.toUpperCase()) {
            case "LAST_WEEK":
                startDate = now.minusWeeks(1);
                break;
            case "LAST_MONTH":
                startDate = now.minusMonths(1);
                break;
            case "LAST_YEAR":
                startDate = now.minusYears(1);
                break;
            case "ALL":
            default:
                return orderRepository.findAll(pageable).map(orderMapper::toDto);
        }

        return orderRepository.findByCreatedAtBetween(startDate, now, pageable)
                .map(orderMapper::toDto);
    }


    @Override
    public OrderDto updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
}
