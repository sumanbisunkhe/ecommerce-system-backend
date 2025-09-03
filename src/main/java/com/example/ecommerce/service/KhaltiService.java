package com.example.ecommerce.service;

import com.example.ecommerce.dto.LookupResponse;
import com.example.ecommerce.dto.PaymentInitiateResponse;
import com.example.ecommerce.entity.User;

public interface KhaltiService {
    PaymentInitiateResponse initiatePayment(String orderId, String orderName, int amount, User user);
    LookupResponse lookupPayment(String pidx);
}
