package com.foodflow.payment.service;

import com.foodflow.payment.dto.PaymentResponse;
import com.foodflow.payment.dto.ProcessPaymentRequest;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse processPayment(ProcessPaymentRequest request);
    PaymentResponse getPaymentById(UUID id);
    PaymentResponse getPaymentByOrderId(UUID orderId);
    PaymentResponse refundPayment(UUID id);
}
