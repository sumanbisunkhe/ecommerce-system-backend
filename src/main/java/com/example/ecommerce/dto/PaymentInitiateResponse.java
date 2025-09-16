package com.example.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentInitiateResponse {
    private String pidx;
    private String payment_url;
    private String expires_at;
    private String purchase_order_id;
    private String purchase_order_name;
}
