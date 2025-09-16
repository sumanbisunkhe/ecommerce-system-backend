package com.example.ecommerce.service;

import com.example.ecommerce.dto.LookupResponse;
import com.example.ecommerce.dto.PaymentCallbackResponse;
import com.example.ecommerce.dto.PaymentInitiateResponse;
import com.example.ecommerce.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentInitiateResponse initiatePayment(Long orderId);

    LookupResponse verifyPayment(String pidx);

    void handleCallback(PaymentCallbackResponse callbackResponse);

    Payment getPaymentById(Long id);

    Page<Payment> getAllPayments(String filter, Pageable pageable);

    Page<Payment> getUserPayments(Long userId, String filter, Pageable pageable);
}
