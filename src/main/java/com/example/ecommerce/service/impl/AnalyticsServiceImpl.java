package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.AnalyticsDto;
import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.UserAnalyticsDto;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.enums.Gender;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.enums.PaymentStatus;
import com.example.ecommerce.mapper.AnalyticsMapper;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final AnalyticsMapper analyticsMapper;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDto getSystemAnalytics() {
        AnalyticsDto analyticsDto = new AnalyticsDto();

        // User analytics
        setUserAnalytics(analyticsDto);

        // Product analytics
        setProductAnalytics(analyticsDto);

        // Category analytics
        setCategoryAnalytics(analyticsDto);

        // Cart analytics
        setCartAnalytics(analyticsDto);

        // Order analytics
        setOrderAnalytics(analyticsDto);

        // Payment analytics
        setPaymentAnalytics(analyticsDto);

        // Revenue analytics - for system analytics, calculate total revenue and last month's revenue
        setRevenueAnalytics(analyticsDto);

        return analyticsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDto getAnalyticsByDate(LocalDate date) {
        return analyticsRepository.findByAnalyticsDate(date)
                .map(analyticsMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Analytics not found for date: " + date));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsDto> getAnalyticsByDateRange(LocalDate startDate, LocalDate endDate) {
        return analyticsRepository.findByDateRange(startDate, endDate)
                .stream()
                .map(analyticsMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnalyticsDto generateDailyAnalytics() {
        Analytics analytics = Analytics.builder()
                .analyticsDate(LocalDate.now())
                .build();

        // User analytics
        setUserAnalytics(analytics);

        // Product analytics
        setProductAnalytics(analytics);

        // Category analytics
        setCategoryAnalytics(analytics);

        // Cart analytics
        setCartAnalytics(analytics);

        // Order analytics
        setOrderAnalytics(analytics);

        // Payment analytics
        setPaymentAnalytics(analytics);

        // Revenue analytics - for daily analytics, calculate daily revenue and last month's revenue
        setDailyRevenueAnalytics(analytics, LocalDate.now());

        Analytics savedAnalytics = analyticsRepository.save(analytics);
        return analyticsMapper.toDTO(savedAnalytics);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void saveDailyAnalytics() {
        try {
            if (!analyticsRepository.existsByAnalyticsDate(LocalDate.now())) {
                generateDailyAnalytics();
                log.info("Daily analytics saved successfully for date: {}", LocalDate.now());
            }
        } catch (Exception e) {
            log.error("Failed to save daily analytics: {}", e.getMessage(), e);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public UserAnalyticsDto getUserAnalytics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return buildUserAnalyticsDto(user);
    }

    private UserAnalyticsDto buildUserAnalyticsDto(User user) {
        List<Order> userOrders = orderRepository.findByUser(user);
        List<Payment> userPayments = paymentRepository.findByUser(user);
        Optional<Cart> userCart = cartRepository.findByUser(user);

        // Calculate orders in last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int ordersLast30Days = (int) userOrders.stream()
                .filter(order -> order.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();

        // Calculate total spent from completed payments
        BigDecimal totalSpent = userPayments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get last order
        Optional<Order> lastOrder = userOrders.stream()
                .max(Comparator.comparing(Order::getCreatedAt));

        // Calculate average order value
        BigDecimal averageOrderValue = userOrders.isEmpty() ? BigDecimal.ZERO :
                totalSpent.divide(BigDecimal.valueOf(userOrders.size()), 2, BigDecimal.ROUND_HALF_UP);

        // Get favorite categories
        List<String> favoriteCategories = getFavoriteCategories(user);

        // Determine loyalty tier based on total spent
        String loyaltyTier = determineLoyaltyTier(totalSpent);

        return UserAnalyticsDto.builder()
                .isActive(user.isActive())
                .joinedDate(user.getCreatedAt())

                // Order summary
                .totalOrders(userOrders.size())
                .pendingOrders((int) userOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count())
                .deliveredOrders((int) userOrders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count())
                .cancelledOrders((int) userOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count())

                // Financial summary
                .totalSpent(totalSpent)
                .averageOrderValue(averageOrderValue)
                .lastOrderAmount(lastOrder.map(Order::getTotalAmount).orElse(BigDecimal.ZERO))
                .lastOrderDate(lastOrder.map(Order::getCreatedAt).orElse(null))

                // Current cart status
                .cartItemsCount(userCart.map(cart -> cart.getItems().size()).orElse(0))
                .cartTotalValue(userCart.map(Cart::getTotalPrice).orElse(BigDecimal.ZERO))

                // Payment summary
                .totalPayments(userPayments.size())
                .successfulPayments((int) userPayments.stream().filter(p -> p.getStatus() == PaymentStatus.COMPLETED).count())
                .failedPayments((int) userPayments.stream().filter(p -> p.getStatus() == PaymentStatus.FAILED).count())

                // Favorite categories
                .favoriteCategories(favoriteCategories)

                // Activity metrics
                .loyaltyTier(loyaltyTier)
                .ordersLast30Days(ordersLast30Days)
                .build();
    }

    private List<String> getFavoriteCategories(User user) {
        List<Order> userOrders = orderRepository.findByUser(user);

        // Count category occurrences
        Map<String, Integer> categoryCount = new HashMap<>();
        for (Order order : userOrders) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() != null && item.getProduct().getCategory() != null) {
                    String categoryName = item.getProduct().getCategory().getName();
                    categoryCount.put(categoryName, categoryCount.getOrDefault(categoryName, 0) + 1);
                }
            }
        }

        // Get top 3 category names
        return categoryCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private String determineLoyaltyTier(BigDecimal totalSpent) {
        if (totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return "Gold";
        } else if (totalSpent.compareTo(new BigDecimal("500")) >= 0) {
            return "Silver";
        } else if (totalSpent.compareTo(new BigDecimal("100")) >= 0) {
            return "Bronze";
        } else {
            return "New Customer";
        }
    }
    private void setUserAnalytics(Analytics analytics) {
        List<User> users = userRepository.findAll();

        analytics.setTotalUsers(users.size());
        analytics.setActiveUsers((int) users.stream().filter(User::isActive).count());
        analytics.setInactiveUsers((int) users.stream().filter(user -> !user.isActive()).count());

        // Fix gender comparison with null safety
        analytics.setMaleUsers((int) users.stream()
                .filter(user -> user.getGender() != null && Gender.MALE.equals(user.getGender()))
                .count());

        analytics.setFemaleUsers((int) users.stream()
                .filter(user -> user.getGender() != null && Gender.FEMALE.equals(user.getGender()))
                .count());

        analytics.setOtherGenderUsers((int) users.stream()
                .filter(user -> user.getGender() != null && Gender.OTHER.equals(user.getGender()))
                .count());

        // Age calculation based on account creation date (temporary)
        LocalDate now = LocalDate.now();
        analytics.setUsersFromTenToTwenty((int) users.stream()
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) >= 10)
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) < 20)
                .count());

        analytics.setUsersFromTwentyToThirty((int) users.stream()
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) >= 20)
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) < 30)
                .count());

        analytics.setUsersAboveThirty((int) users.stream()
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) >= 30)
                .count());
    }

    private void setUserAnalytics(AnalyticsDto analyticsDto) {
        List<User> users = userRepository.findAll();

        analyticsDto.setTotalUsers(users.size());
        analyticsDto.setActiveUsers((int) users.stream().filter(User::isActive).count());
        analyticsDto.setInactiveUsers((int) users.stream().filter(user -> !user.isActive()).count());

        // Fix gender comparison with null safety
        analyticsDto.setMaleUsers((int) users.stream()
                .filter(user -> user.getGender() != null && Gender.MALE.equals(user.getGender()))
                .count());

        analyticsDto.setFemaleUsers((int) users.stream()
                .filter(user -> user.getGender() != null && Gender.FEMALE.equals(user.getGender()))
                .count());

        analyticsDto.setOtherGenderUsers((int) users.stream()
                .filter(user -> user.getGender() != null && Gender.OTHER.equals(user.getGender()))
                .count());

        // Age calculation based on account creation date (temporary)
        LocalDate now = LocalDate.now();
        analyticsDto.setUsersFromTenToTwenty((int) users.stream()
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) >= 10)
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) < 20)
                .count());

        analyticsDto.setUsersFromTwentyToThirty((int) users.stream()
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) >= 20)
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) < 30)
                .count());

        analyticsDto.setUsersAboveThirty((int) users.stream()
                .filter(user -> calculateAge(user.getCreatedAt().toLocalDate(), now) >= 30)
                .count());
    }

    private void setProductAnalytics(Analytics analytics) {
        List<Product> products = productRepository.findAll();

        analytics.setTotalProducts(products.size());
        analytics.setActiveProducts((int) products.stream().filter(Product::getActive).count());
        analytics.setInactiveProducts((int) products.stream().filter(product -> !product.getActive()).count());
    }

    private void setProductAnalytics(AnalyticsDto analyticsDto) {
        List<Product> products = productRepository.findAll();

        analyticsDto.setTotalProducts(products.size());
        analyticsDto.setActiveProducts((int) products.stream().filter(Product::getActive).count());
        analyticsDto.setInactiveProducts((int) products.stream().filter(product -> !product.getActive()).count());

        // New products (most recent 5)
        analyticsDto.setNewProducts(productRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList()));

        // Popular products (5 most bought)
        analyticsDto.setPopularProducts(orderItemRepository.findTop5ProductsByQuantity()
                .stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList()));
    }

    private void setCategoryAnalytics(Analytics analytics) {
        analytics.setTotalCategories((int) categoryRepository.count());
    }

    private void setCategoryAnalytics(AnalyticsDto analyticsDto) {
        analyticsDto.setTotalCategories((int) categoryRepository.count());

        // New categories (most recent 5)
        analyticsDto.setNewCategories(categoryRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToCategoryDTO)
                .collect(Collectors.toList()));
    }

    private void setCartAnalytics(Analytics analytics) {
        analytics.setTotalCarts((int) cartRepository.count());
    }

    private void setCartAnalytics(AnalyticsDto analyticsDto) {
        analyticsDto.setTotalCarts((int) cartRepository.count());
    }

    private void setOrderAnalytics(Analytics analytics) {
        List<Order> orders = orderRepository.findAll();

        analytics.setTotalOrders(orders.size());

        // Use proper enum comparison with null safety
        analytics.setPendingOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.PENDING.equals(order.getStatus()))
                .count());

        analytics.setConfirmedOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.CONFIRMED.equals(order.getStatus()))
                .count());

        analytics.setShippedOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.SHIPPED.equals(order.getStatus()))
                .count());

        analytics.setDeliveredOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.DELIVERED.equals(order.getStatus()))
                .count());

        analytics.setCancelledOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.CANCELLED.equals(order.getStatus()))
                .count());
    }

    private void setOrderAnalytics(AnalyticsDto analyticsDto) {
        List<Order> orders = orderRepository.findAll();

        analyticsDto.setTotalOrders(orders.size());

        // Use proper enum comparison with null safety
        analyticsDto.setPendingOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.PENDING.equals(order.getStatus()))
                .count());

        analyticsDto.setConfirmedOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.CONFIRMED.equals(order.getStatus()))
                .count());

        analyticsDto.setShippedOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.SHIPPED.equals(order.getStatus()))
                .count());

        analyticsDto.setDeliveredOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.DELIVERED.equals(order.getStatus()))
                .count());

        analyticsDto.setCancelledOrders((int) orders.stream()
                .filter(order -> order.getStatus() != null && OrderStatus.CANCELLED.equals(order.getStatus()))
                .count());
    }

    private void setPaymentAnalytics(Analytics analytics) {
        List<Payment> payments = paymentRepository.findAll();

        analytics.setTotalPayments(payments.size());

        // Only KHALTI and CASH_ON_DELIVERY payment methods with null safety
        analytics.setPaymentViaKhalti((int) payments.stream()
                .filter(payment -> payment.getMethod() != null && "KHALTI".equalsIgnoreCase(payment.getMethod().toString()))
                .count());

        analytics.setPaymentViaCashOnDelivery((int) payments.stream()
                .filter(payment -> payment.getMethod() != null && "CASH_ON_DELIVERY".equalsIgnoreCase(payment.getMethod().toString()))
                .count());

        // Set other payment methods to 0 since we only have two
        analytics.setPaymentViaCreditCard(0);
        analytics.setPaymentViaDebitCard(0);
        analytics.setPaymentViaEsewa(0);
        analytics.setPaymentViaBankTransfer(0);

        // Payment status comparisons with null safety
        analytics.setPendingPayment((int) payments.stream()
                .filter(payment -> payment.getStatus() != null && PaymentStatus.PENDING.equals(payment.getStatus()))
                .count());

        analytics.setCompletedPayment((int) payments.stream()
                .filter(payment -> payment.getStatus() != null && PaymentStatus.COMPLETED.equals(payment.getStatus()))
                .count());

        analytics.setFailedPayment((int) payments.stream()
                .filter(payment -> payment.getStatus() != null && PaymentStatus.FAILED.equals(payment.getStatus()))
                .count());

        analytics.setRefundedPayment((int) payments.stream()
                .filter(payment -> payment.getStatus() != null && PaymentStatus.REFUNDED.equals(payment.getStatus()))
                .count());
    }

    private void setPaymentAnalytics(AnalyticsDto analyticsDto) {
        List<Payment> payments = paymentRepository.findAll();

        analyticsDto.setTotalPayments(payments.size());

        // Only KHALTI and CASH_ON_DELIVERY payment methods with null safety
        analyticsDto.setPaymentViaKhalti((int) payments.stream()
                .filter(payment -> payment.getMethod() != null && "KHALTI".equalsIgnoreCase(payment.getMethod().toString()))
                .count());

        analyticsDto.setPaymentViaCashOnDelivery((int) payments.stream()
                .filter(payment -> payment.getMethod() != null && "CASH_ON_DELIVERY".equalsIgnoreCase(payment.getMethod().toString()))
                .count());

        // Set other payment methods to 0
        analyticsDto.setPaymentViaCreditCard(0);
        analyticsDto.setPaymentViaDebitCard(0);
        analyticsDto.setPaymentViaEsewa(0);
        analyticsDto.setPaymentViaBankTransfer(0);

        // Payment status comparisons with null safety
        analyticsDto.setPendingPayment((int) payments.stream()
                .filter(payment -> payment.getStatus() != null && PaymentStatus.PENDING.equals(payment.getStatus()))
                .count());

        analyticsDto.setCompletedPayment((int) payments.stream()
                .filter(payment -> payment.getStatus() != null && PaymentStatus.COMPLETED.equals(payment.getStatus()))
                .count());

        analyticsDto.setFailedPayment((int) payments.stream()
                .filter(payment -> payment.getStatus() != null && PaymentStatus.FAILED.equals(payment.getStatus()))
                .count());

        analyticsDto.setRefundedPayment((int) payments.stream()
                .filter(payment -> payment.getStatus() != null && PaymentStatus.REFUNDED.equals(payment.getStatus()))
                .count());
    }

    private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }
    // Add this method to calculate total revenue from completed payments
    private BigDecimal calculateTotalRevenue() {
        List<Payment> completedPayments = paymentRepository.findByStatus(PaymentStatus.COMPLETED);
        return completedPayments.stream()
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Add this method to calculate daily revenue for a specific date
    private BigDecimal calculateDailyRevenue(LocalDate date) {
        List<Payment> dailyCompletedPayments = paymentRepository.findByStatusAndCreatedAtBetween(
                PaymentStatus.COMPLETED,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        );
        return dailyCompletedPayments.stream()
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void setRevenueAnalytics(Analytics analytics) {
        analytics.setTotalRevenue(calculateTotalRevenue());
        analytics.setTotalRevenueLastMonth(calculateLastMonthRevenue());
    }

    private void setRevenueAnalytics(AnalyticsDto analyticsDto) {
        analyticsDto.setTotalRevenue(calculateTotalRevenue());
        analyticsDto.setTotalRevenueLastMonth(calculateLastMonthRevenue());
    }

    private void setDailyRevenueAnalytics(Analytics analytics, LocalDate date) {
        analytics.setTotalRevenue(calculateDailyRevenue(date));
        analytics.setTotalRevenueLastMonth(calculateLastMonthRevenueForDate(date));
    }

    // Add this method to calculate last month's total revenue
    private BigDecimal calculateLastMonthRevenue() {
        LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = firstDayOfLastMonth.plusMonths(1).minusDays(1);

        List<Payment> lastMonthCompletedPayments = paymentRepository.findByStatusAndCreatedAtBetween(
                PaymentStatus.COMPLETED,
                firstDayOfLastMonth.atStartOfDay(),
                lastDayOfLastMonth.atTime(23, 59, 59)
        );

        return lastMonthCompletedPayments.stream()
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Add this method to calculate last month's revenue for a specific date
    private BigDecimal calculateLastMonthRevenueForDate(LocalDate date) {
        LocalDate firstDayOfLastMonth = date.minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = firstDayOfLastMonth.plusMonths(1).minusDays(1);

        List<Payment> lastMonthCompletedPayments = paymentRepository.findByStatusAndCreatedAtBetween(
                PaymentStatus.COMPLETED,
                firstDayOfLastMonth.atStartOfDay(),
                lastDayOfLastMonth.atTime(23, 59, 59)
        );

        return lastMonthCompletedPayments.stream()
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private ProductDto mapToProductDTO(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.getActive())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private CategoryDto mapToCategoryDTO(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}