package com.example.ecommerce.controller;

import com.example.ecommerce.dto.LookupResponse;
import com.example.ecommerce.dto.PaymentCallbackResponse;
import com.example.ecommerce.dto.PaymentInitiateResponse;
import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentInitiateResponse>> initiatePayment(@RequestParam Long orderId) {
        PaymentInitiateResponse response = paymentService.initiatePayment(orderId);
        return ResponseEntity.ok(ApiResponse.success("Payment initiated", response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<LookupResponse>> verifyPayment(@RequestParam String pidx) {
        LookupResponse response = paymentService.verifyPayment(pidx);
        return ResponseEntity.ok(ApiResponse.success("Payment verified", response));
    }

    @PostMapping("/khalti/callback")
    public ResponseEntity<ApiResponse<PaymentCallbackResponse>> handleCallback(
            @RequestParam String pidx) {

        var lookup = paymentService.verifyPayment(pidx);

        PaymentCallbackResponse callback = new PaymentCallbackResponse();
        callback.setPidx(pidx);
        callback.setTxnId(lookup.getTransaction_id());
        callback.setStatus(lookup.getStatus());

        paymentService.handleCallback(callback);

        return ResponseEntity.ok(ApiResponse.success("Payment callback processed", callback));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success("Payment fetched", payment));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<Payment>>> getAllPayments(
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
        );

        Page<Payment> payments = paymentService.getAllPayments(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success("Payments fetched", payments));
    }


}
