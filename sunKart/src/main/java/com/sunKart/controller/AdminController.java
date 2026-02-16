package com.sunKart.controller;

import com.sunKart.model.Order;
import com.sunKart.model.Product;
import com.sunKart.service.OrderService;
import com.sunKart.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        List<Product> products = productService.getAllProducts();
        List<Order> orders = orderService.getAllOrders();
        
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("recentOrders", orders.stream().limit(5).toList());
        
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String manageProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/product-form";
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, @ModelAttribute Product product) {
        product.setId(id);
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderService.getOrderById(id);
        order.setStatus(Order.OrderStatus.valueOf(status));
        orderService.saveOrder(order);
        return "redirect:/admin/orders";
    }
}
