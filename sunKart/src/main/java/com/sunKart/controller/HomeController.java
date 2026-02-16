package com.sunKart.controller;

import com.sunKart.model.Product;
import com.sunKart.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String category,
                          @RequestParam(required = false) String search,
                          Model model) {
        List<Product> products;
        
        // Priority: Search > Category > All
        if (search != null && !search.trim().isEmpty()) {
            // Search functionality
            products = productService.searchProducts(search);
            model.addAttribute("searchKeyword", search);
        } else if (category != null && !category.isEmpty()) {
            // Category filter
            products = productService.getProductsByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else {
            // Show all products
            products = productService.getAllProducts();
        }
        
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-details";
    }
}