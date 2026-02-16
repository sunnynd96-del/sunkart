package com.sunKart.controller;

import com.sunKart.model.Cart;
import com.sunKart.model.User;
import com.sunKart.service.CartService;
import com.sunKart.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Cart> cartItems = cartService.getCartItems(user);
        Double total = cartService.getCartTotal(user);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam Long productId,
                           @RequestParam(defaultValue = "1") Integer quantity) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        cartService.addToCart(user, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update/{id}")
    public String updateQuantity(@PathVariable Long id,
                                 @RequestParam Integer quantity) {
        cartService.updateCartQuantity(id, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        cartService.clearCart(user);
        return "redirect:/cart";
    }
}
