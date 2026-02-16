package com.sunKart.service;

import com.sunKart.model.Cart;
import com.sunKart.model.Product;
import com.sunKart.model.User;
import com.sunKart.repository.CartRepository;
import com.sunKart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<Cart> getCartItems(User user) {
        return cartRepository.findByUser(user);
    }

    @Transactional
    public Cart addToCart(User user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Cart> existingCart = cartRepository.findByUserAndProductId(user, productId);

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            cart.setTotalPrice(cart.getProduct().getPrice() * cart.getQuantity());
            return cartRepository.save(cart);
        } else {
            Cart cart = new Cart(user, product, quantity);
            return cartRepository.save(cart);
        }
    }

    @Transactional
    public void updateCartQuantity(Long cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cart.setQuantity(quantity);
        cart.setTotalPrice(cart.getProduct().getPrice() * quantity);
        cartRepository.save(cart);
    }

    @Transactional
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }

    public Double getCartTotal(User user) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        return cartItems.stream()
                .mapToDouble(Cart::getTotalPrice)
                .sum();
    }
}
