package com.foodflow.payment.service.impl;

import com.foodflow.payment.dto.PaymentResponse;
import com.foodflow.payment.dto.ProcessPaymentRequest;
import com.foodflow.payment.entity.Payment;
import com.foodflow.payment.entity.PaymentStatus;
import com.foodflow.payment.exception.BadRequestException;
import com.foodflow.payment.exception.ResourceNotFoundException;
import com.foodflow.payment.repository.PaymentRepository;
import com.foodflow.payment.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        paymentRepository.findByOrderId(request.getOrderId()).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.SUCCESS) {
                throw new BadRequestException("Payment already processed for order: " + request.getOrderId());
            }
        });

        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());

        String txnRef = "TXN-2026-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.setTransactionReference(txnRef);

        if (Boolean.TRUE.equals(request.getSimulateFailure())) {
            payment.setStatus(PaymentStatus.FAILED);
        } else {
            payment.setStatus(PaymentStatus.SUCCESS);
        }

        return toResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order " + orderId + " not found"));
        return toResponse(payment);
    }

    @Override
    public PaymentResponse refundPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Only successful payments can be refunded. Current status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        return toResponse(paymentRepository.save(payment));
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse res = new PaymentResponse();
        res.setId(payment.getId());
        res.setOrderId(payment.getOrderId());
        res.setUserId(payment.getUserId());
        res.setAmount(payment.getAmount());
        res.setMethod(payment.getMethod());
        res.setStatus(payment.getStatus());
        res.setTransactionReference(payment.getTransactionReference());
        res.setCreatedAt(payment.getCreatedAt());
        res.setUpdatedAt(payment.getUpdatedAt());
        return res;
    }
}
