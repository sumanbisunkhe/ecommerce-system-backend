package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.KhaltiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KhaltiServiceImpl implements KhaltiService {

    private final RestTemplate restTemplate;

    @Value("${khalti.base.url}")
    private String baseUrl;

    @Value("${khalti.secret.key}")
    private String secretKey;

    @Value("${khalti.return.url}")
    private String returnUrl;

    @Value("${khalti.website.url}")
    private String websiteUrl;

    public KhaltiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentInitiateResponse initiatePayment(String orderId, String orderName, int amount, User user) {
        PaymentInitiateRequest request = new PaymentInitiateRequest();
        request.setPurchase_order_id(orderId);
        request.setPurchase_order_name(orderName);

        // Convert amount to paisa
        int amountInPaisa = amount * 100;
        request.setAmount(amountInPaisa);

        request.setReturn_url(returnUrl);
        request.setWebsite_url(websiteUrl);

        CustomerInfo customerInfo = new CustomerInfo(
                user.getFirstName() + " " + (user.getMiddleName() != null ? user.getMiddleName() + " " : "") + user.getLastName(),
                user.getEmail(),
                user.getUsername() // replace with phone if available
        );
        request.setCustomer_info(customerInfo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", secretKey);

        HttpEntity<PaymentInitiateRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<PaymentInitiateResponse> response = restTemplate.exchange(
                baseUrl + "/epayment/initiate/",
                HttpMethod.POST,
                entity,
                PaymentInitiateResponse.class
        );

        return response.getBody();
    }

    @Override
    public LookupResponse lookupPayment(String pidx) {
        LookupRequest request = new LookupRequest(pidx);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", secretKey);

        HttpEntity<LookupRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<LookupResponse> response = restTemplate.exchange(
                baseUrl + "/epayment/lookup/",
                HttpMethod.POST,
                entity,
                LookupResponse.class
        );

        return response.getBody();
    }
}
