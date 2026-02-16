package com.sunKart.service;

import com.sunKart.model.Cart;
import com.sunKart.model.Order;
import com.sunKart.model.OrderItem;
import com.sunKart.model.User;
import com.sunKart.repository.CartRepository;
import com.sunKart.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Order createOrder(User user, String deliveryAddress, String paymentMethod) {
        // Get cart items
        List<Cart> cartItems = cartRepository.findByUser(user);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Calculate total
        double totalAmount = cartItems.stream()
                .mapToDouble(Cart::getTotalPrice)
                .sum();
        
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(Order.OrderStatus.PENDING);
        
        // Create order items from cart
        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            order.getOrderItems().add(orderItem);
        }
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart
        cartRepository.deleteByUser(user);
        
        return savedOrder;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    // ✅ DELETE METHOD
    @Transactional
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}