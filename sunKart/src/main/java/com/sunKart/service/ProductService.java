package com.sunKart.service;

import com.sunKart.model.Product;
import com.sunKart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // ✅ SEARCH METHOD
    public List<Product> searchProducts(String keyword) {
        List<Product> allProducts = productRepository.findAll();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return allProducts;
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        
        return allProducts.stream()
                .filter(product -> 
                    product.getName().toLowerCase().contains(searchTerm) ||
                    product.getDescription().toLowerCase().contains(searchTerm) ||
                    product.getCategory().toLowerCase().contains(searchTerm) ||
                    (product.getBrand() != null && product.getBrand().toLowerCase().contains(searchTerm))
                )
                .collect(Collectors.toList());
    }
}