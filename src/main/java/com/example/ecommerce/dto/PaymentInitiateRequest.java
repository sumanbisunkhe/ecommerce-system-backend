package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class PaymentInitiateRequest {
    private String return_url;
    private String website_url;
    private int amount;
    private String purchase_order_id;
    private String purchase_order_name;
    private CustomerInfo customer_info;
}
