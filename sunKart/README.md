# SunKart E-Commerce Application

## 🚀 Quick Start Guide

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+

### Step 1: Database Setup

Open MySQL and run:
```sql
CREATE DATABASE sunkart_db;
```

### Step 2: Configure Database

Edit `src/main/resources/application.properties` if needed:
```properties
spring.datasource.username=root
spring.datasource.password=1234
```

### Step 3: Run the Application

#### Using Maven:
```bash
mvn spring-boot:run
```

#### Or using IDE:
Run `SunKartApplication.java` as Java Application

### Step 4: Access the Application

Open your browser: `http://localhost:8080`

## 📝 Initial Setup

### Create Admin User

Run this SQL after first startup:
```sql
USE sunkart_db;

-- Create roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Create admin user (password: admin123)
INSERT INTO users (username, email, password, enabled) 
VALUES ('admin', 'admin@sunkart.com', '$2a$10$8JqcEMtWzPqVwQxzQz0zb.KJ4xqYBxCVHNQGpK6wO4ZkGPxVqBBrK', true);

-- Assign admin role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';
```

### Default Login Credentials

**Admin:**
- Username: `admin`
- Password: `admin123`

**Regular User:**
- Register through `/register` page

## 📂 Project Structure

```
sunKart/
├── src/main/java/com/sunKart/
│   ├── SunKartApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── HomeController.java
│   │   ├── AuthController.java
│   │   ├── CartController.java
│   │   ├── OrderController.java
│   │   └── AdminController.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Product.java
│   │   ├── Cart.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── RoleRepository.java
│   │   ├── ProductRepository.java
│   │   ├── CartRepository.java
│   │   ├── OrderRepository.java
│   │   └── OrderItemRepository.java
│   ├── security/
│   │   ├── CustomUserDetails.java
│   │   └── CustomUserDetailsService.java
│   └── service/
│       ├── UserService.java
│       ├── ProductService.java
│       ├── CartService.java
│       └── OrderService.java
├── src/main/resources/
│   ├── application.properties
│   └── templates/
│       ├── index.html
│       ├── login.html
│       ├── register.html
│       └── cart.html
└── pom.xml
```

## 🎯 Features

✅ User Authentication & Authorization
✅ Product Browsing & Search
✅ Shopping Cart Management
✅ Order Placement & Tracking
✅ Admin Panel for Product & Order Management
✅ Role-based Access Control

## 🔧 Common Issues & Solutions

### Issue: MySQL Connection Failed
**Solution:** Check if MySQL is running and credentials are correct in `application.properties`

### Issue: Port 8080 already in use
**Solution:** Change port in application.properties:
```properties
server.port=8081
```

### Issue: Table doesn't exist
**Solution:** Make sure `spring.jpa.hibernate.ddl-auto=update` is set

## 📱 API Endpoints

### Public
- `GET /` - Home page
- `GET /login` - Login page
- `POST /login` - Login submit
- `GET /register` - Registration page
- `POST /register` - Registration submit
- `GET /products` - Product listing
- `GET /products/{id}` - Product details

### User (Authenticated)
- `GET /cart` - View cart
- `POST /cart/add` - Add to cart
- `POST /cart/update/{id}` - Update cart item
- `POST /cart/remove/{id}` - Remove from cart
- `GET /checkout` - Checkout page
- `POST /checkout` - Place order
- `GET /orders` - My orders
- `GET /orders/{id}` - Order details

### Admin Only
- `GET /admin` - Admin dashboard
- `GET /admin/products` - Manage products
- `POST /admin/products/add` - Add product
- `POST /admin/products/edit/{id}` - Edit product
- `POST /admin/products/delete/{id}` - Delete product
- `GET /admin/orders` - Manage orders
- `POST /admin/orders/{id}/status` - Update order status

## 🎨 Technologies Used

- **Backend:** Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf, Bootstrap 5
- **Database:** MySQL 8.0
- **Build Tool:** Maven
- **Java Version:** 17

## 📧 Support

For issues or questions, contact: support@sunkart.com

---

**Happy Shopping! 🛒**
