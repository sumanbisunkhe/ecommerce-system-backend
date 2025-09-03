package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class LookupResponse {
    private String pidx;
    private String status; // Completed, Pending, Failed
    private String transaction_id;
}
