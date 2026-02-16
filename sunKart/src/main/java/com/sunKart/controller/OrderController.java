package com.sunKart.controller;

import com.sunKart.model.Order;
import com.sunKart.model.User;
import com.sunKart.service.CartService;
import com.sunKart.service.OrderService;
import com.sunKart.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;

    public OrderController(OrderService orderService, UserService userService, CartService cartService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/checkout")
    public String checkoutPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Double total = cartService.getCartTotal(user);
        
        if (total == 0) {
            return "redirect:/cart";
        }
        
        model.addAttribute("total", total);
        model.addAttribute("user", user);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam String address,
                            @RequestParam String paymentMethod,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if cart is empty
            if (cartService.getCartItems(user).isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Your cart is empty!");
                return "redirect:/cart";
            }
            
            // Create order
            Order order = orderService.createOrder(user, address, paymentMethod);
            
            redirectAttributes.addFlashAttribute("success", "Order placed successfully! Order ID: #" + order.getId());
            return "redirect:/orders/" + order.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to place order: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/orders")
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Order> orders = orderService.getUserOrders(user);
        model.addAttribute("orders", orders);
        return "my-orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, 
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Order order = orderService.getOrderById(id);
        
        // Verify order belongs to user (security check)
        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/orders";
        }
        
        model.addAttribute("order", order);
        return "order-details";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Order order = orderService.getOrderById(id);
            
            // Security check: Verify order belongs to user
            if (!order.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access!");
                return "redirect:/orders";
            }
            
            // Check if order can be cancelled
            if (order.getStatus() == Order.OrderStatus.DELIVERED) {
                redirectAttributes.addFlashAttribute("error", "Cannot cancel a delivered order!");
                return "redirect:/orders/" + id;
            }
            
            if (order.getStatus() == Order.OrderStatus.CANCELLED) {
                redirectAttributes.addFlashAttribute("error", "Order is already cancelled!");
                return "redirect:/orders/" + id;
            }
            
            // Cancel the order
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderService.saveOrder(order);
            
            redirectAttributes.addFlashAttribute("success", "Order #" + id + " has been cancelled successfully!");
            return "redirect:/orders/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel order: " + e.getMessage());
            return "redirect:/orders/" + id;
        }
    }

    // ✅ DELETE ORDER METHOD
    @PostMapping("/orders/{id}/delete")
    public String deleteOrder(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Order order = orderService.getOrderById(id);
            
            // Security check: Verify order belongs to user
            if (!order.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access!");
                return "redirect:/orders";
            }
            
            // Only allow deleting cancelled orders
            if (order.getStatus() != Order.OrderStatus.CANCELLED) {
                redirectAttributes.addFlashAttribute("error", "Only cancelled orders can be deleted!");
                return "redirect:/orders";
            }
            
            // Delete the order
            orderService.deleteOrder(id);
            
            redirectAttributes.addFlashAttribute("success", "Order #" + id + " has been deleted successfully!");
            return "redirect:/orders";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete order: " + e.getMessage());
            return "redirect:/orders";
        }
    }
}