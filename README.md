# 🛍️ E-Commerce Backend System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**A modern, scalable, and feature-rich e-commerce backend built with Spring Boot**

[Features](#-features) • [Installation](#-installation) • [API Documentation](#-api-documentation) • [Architecture](#-architecture) • [Contributing](#-contributing)

</div>

---

## ✨ Features

### 🔐 Authentication & Authorization
- **JWT-based Authentication** with secure token management
- **Role-based Access Control** (Admin, Merchant, Customer)
- **Password encryption** using BCrypt
- **Token expiration handling** with refresh mechanism

### 🛒 E-Commerce Core
- **Product Management** with image upload via Cloudinary
- **Category Management** with hierarchical structure
- **Shopping Cart** with persistent sessions
- **Order Management** with status tracking
- **Payment Integration** with multiple payment methods
- **Inventory Management** with stock tracking

### 📊 Analytics & Recommendations
- **Real-time Analytics** for business insights
- **User Behavior Tracking** for personalized experiences
- **Product Recommendations** based on user preferences
- **Sales Analytics** with comprehensive reporting

### 🚀 Technical Features
- **RESTful API Design** with standardized responses
- **Data Validation** with comprehensive error handling
- **Database Optimization** with JPA/Hibernate
- **File Upload Support** with Cloudinary integration
- **Security Best Practices** with Spring Security
- **Clean Architecture** with layered design pattern

---

## 🏗️ Architecture

```
├── 📁 config/           # Configuration classes
├── 📁 controller/       # REST API controllers
├── 📁 dto/             # Data Transfer Objects
├── 📁 entity/          # JPA entities
├── 📁 enums/           # Enum definitions
├── 📁 exceptions/      # Custom exception handling
├── 📁 mapper/          # MapStruct mappers
├── 📁 repository/      # Data access layer
├── 📁 security/        # Security configurations
└── 📁 service/         # Business logic layer
```

### 🛡️ Security Layer
- JWT token-based authentication
- Method-level security annotations
- CORS configuration for cross-origin requests
- Password encoding with BCrypt

### 💾 Data Layer
- PostgreSQL database with JPA/Hibernate
- Repository pattern for data access
- Entity relationships with proper cascading
- Database migrations and versioning

---

## 🚀 Installation

### Prerequisites
- ☕ **Java 17** or higher
- 🐘 **PostgreSQL 12+**
- 📦 **Maven 3.6+**
- 🌐 **Git**

### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/sumanbisunkhe/ecommerce-system-backend.git
   cd ecommerce-system-backend
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE ecommerce_db;
   CREATE USER ecommerce_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE ecommerce_db TO ecommerce_user;
   ```

3. **Configure Environment**
   ```properties
   # application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # JWT Configuration
   jwt.secret=your-secret-key
   jwt.expiration=3600000
   
   # Cloudinary Configuration
   cloudinary.cloud-name=your-cloud-name
   cloudinary.api-key=your-api-key
   cloudinary.api-secret=your-api-secret
   ```

4. **Build and Run**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

---

## 📚 API Documentation

### 🔐 Authentication Endpoints
```
POST   /auth/register    # User registration
POST   /auth/login       # User login
POST   /auth/refresh     # Token refresh
PUT    /auth/password    # Change password
```

### 🛍️ Product Management
```
GET    /products                    # Get all products (paginated)
GET    /products/{id}              # Get product by ID
POST   /products                   # Create product (Admin/Merchant)
PUT    /products/{id}              # Update product (Admin/Merchant)
DELETE /products/{id}              # Delete product (Admin/Merchant)
GET    /products/category/{categoryId} # Get products by category
GET    /products/search            # Search products
```

### 🛒 Shopping Cart
```
GET    /cart                       # Get user's cart
POST   /cart/items                 # Add item to cart
PUT    /cart/items/{itemId}        # Update cart item
DELETE /cart/items/{itemId}        # Remove item from cart
DELETE /cart                       # Clear cart
```

### 📦 Order Management
```
GET    /orders                     # Get user's orders
GET    /orders/{id}               # Get order details
POST   /orders                    # Create new order
PUT    /orders/{id}/status        # Update order status (Admin)
GET    /orders/analytics          # Order analytics (Admin)
```

### 💳 Payment Processing
```
POST   /payments/initiate         # Initiate payment
POST   /payments/callback         # Payment callback
GET    /payments/{orderId}        # Get payment status
```

### 👥 User Management
```
GET    /users/profile             # Get user profile
PUT    /users/profile             # Update user profile
GET    /users                     # Get all users (Admin)
PUT    /users/{id}/role           # Update user role (Admin)
```

### 📊 Analytics & Recommendations
```
GET    /analytics/dashboard       # Dashboard analytics (Admin)
GET    /analytics/sales           # Sales analytics
GET    /recommendations          # Get user recommendations
POST   /analytics/track          # Track user activity
```

---

## 🛠️ Technology Stack

### Backend Framework
- **Spring Boot 3.5.5** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **Spring Validation** - Input validation

### Database & Storage
- **PostgreSQL** - Primary database
- **Cloudinary** - Image storage and management
- **Hibernate** - ORM framework

### Utilities & Tools
- **MapStruct** - Object mapping
- **Lombok** - Boilerplate code reduction
- **JWT** - Token-based authentication
- **Maven** - Dependency management
- **DotEnv** - Environment variable management

---

## 🔧 Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce_db
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true
```

### Security Configuration
```yaml
jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: ${JWT_EXPIRATION:3600000}
```

### Cloudinary Configuration
```yaml
cloudinary:
  cloud-name: ${CLOUDINARY_CLOUD_NAME:your-cloud-name}
  api-key: ${CLOUDINARY_API_KEY:your-api-key}
  api-secret: ${CLOUDINARY_API_SECRET:your-api-secret}
```

---

## 🧪 Testing

Run the test suite:
```bash
./mvnw test
```

Run with coverage:
```bash
./mvnw test jacoco:report
```

---

## 📖 Project Structure

```
src/
├── main/
│   ├── java/com/example/ecommerce/
│   │   ├── config/                 # Configuration classes
│   │   │   ├── CloudinaryConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/             # REST controllers
│   │   │   ├── AuthController.java
│   │   │   ├── ProductController.java
│   │   │   ├── OrderController.java
│   │   │   └── ...
│   │   ├── dto/                    # Data Transfer Objects
│   │   ├── entity/                 # JPA entities
│   │   ├── enums/                  # Enumerations
│   │   ├── exceptions/             # Exception handling
│   │   ├── mapper/                 # MapStruct mappers
│   │   ├── repository/             # Data access layer
│   │   ├── security/               # Security components
│   │   └── service/                # Business logic
│   └── resources/
│       ├── application.properties
│       └── static/
└── test/                           # Test classes
```

---

## 🚀 Deployment

### Using Docker
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/ecommerce-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:
```bash
docker build -t ecommerce-backend .
docker run -p 8080:8080 ecommerce-backend
```

### Using Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      - DB_URL=jdbc:postgresql://db:5432/ecommerce_db
  
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: ecommerce_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
```

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### Development Guidelines
- Follow Java coding conventions
- Write comprehensive tests
- Update documentation for new features
- Use meaningful commit messages
- Ensure all tests pass before submitting PR

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Suman Bisunkhe**
- GitHub: [@sumanbisunkhe](https://github.com/sumanbisunkhe)
- Email: sumanbisunkhe304@gmail.com

---

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- PostgreSQL community for the robust database
- Cloudinary for seamless image management
- All contributors who helped improve this project

---

## 📊 Project Stats

![GitHub stars](https://img.shields.io/github/stars/sumanbisunkhe/ecommerce-system-backend)
![GitHub forks](https://img.shields.io/github/forks/sumanbisunkhe/ecommerce-system-backend)
![GitHub issues](https://img.shields.io/github/issues/sumanbisunkhe/ecommerce-system-backend)
![GitHub pull requests](https://img.shields.io/github/issues-pr/sumanbisunkhe/ecommerce-system-backend)

---

<div align="center">

Made with ❤️ by [Suman Bisunkhe](https://github.com/sumanbisunkhe)

**If this project helped you, please consider giving it a ⭐!**

</div>