package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class PaymentCallbackResponse {
    private String pidx;
    private String txnId;
    private String status;
}
