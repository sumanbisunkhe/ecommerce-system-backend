package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.LookupResponse;
import com.example.ecommerce.dto.PaymentCallbackResponse;
import com.example.ecommerce.dto.PaymentInitiateResponse;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.enums.PaymentMethod;
import com.example.ecommerce.enums.PaymentStatus;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.service.KhaltiService;
import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final KhaltiService khaltiService;

    @Transactional
    @Override
    public PaymentInitiateResponse initiatePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        PaymentInitiateResponse response = khaltiService.initiatePayment(
                String.valueOf(order.getId()),
                "Order-" + order.getId(),
                order.getTotalAmount().intValue(),
                order.getUser()
        );

        Payment payment = Payment.builder()
                .order(order)
                .method(PaymentMethod.KHALTI)
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .transactionId(response.getPidx())
                .build();

        paymentRepository.save(payment);

        return response;
    }

    @Override
    public LookupResponse verifyPayment(String pidx) {
        LookupResponse lookupResponse = khaltiService.lookupPayment(pidx);

        Payment payment = paymentRepository.findByTransactionId(pidx)
                .orElseThrow(() -> new RuntimeException("Payment not found for transaction: " + pidx));

        if ("Completed".equalsIgnoreCase(lookupResponse.getStatus())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.getOrder().setPaymentStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.getOrder().setPaymentStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);

        return lookupResponse;
    }


    @Override
    public void handleCallback(PaymentCallbackResponse callbackResponse) {
        verifyPayment(callbackResponse.getPidx());
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    @Override
    public Page<Payment> getAllPayments(String filter, Pageable pageable) {
        LocalDateTime start = null;
        LocalDateTime now = LocalDateTime.now();

        switch (filter != null ? filter.toLowerCase() : "") {
            case "today":
                start = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "lastweek":
                start = now.minusWeeks(1);
                break;
            case "15days":
                start = now.minusDays(15);
                break;
            case "lastmonth":
                start = now.minusMonths(1);
                break;
            case "lastyear":
                start = now.minusYears(1);
                break;
        }

        if (start != null) {
            return paymentRepository.findAllByCreatedAtAfter(start, pageable);
        } else {
            return paymentRepository.findAll(pageable);
        }
    }
}
