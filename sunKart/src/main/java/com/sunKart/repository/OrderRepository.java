package com.sunKart.repository;

import com.sunKart.model.Order;
import com.sunKart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find all orders by user, sorted by order date descending (newest first)
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    // Find all orders sorted by order date descending
    List<Order> findAllByOrderByOrderDateDesc();
}