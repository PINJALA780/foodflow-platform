package com.foodflow.payment.service;

import com.foodflow.payment.dto.PaymentResponse;
import com.foodflow.payment.dto.ProcessPaymentRequest;
import com.foodflow.payment.entity.Payment;
import com.foodflow.payment.entity.PaymentMethod;
import com.foodflow.payment.entity.PaymentStatus;
import com.foodflow.payment.exception.BadRequestException;
import com.foodflow.payment.repository.PaymentRepository;
import com.foodflow.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private UUID sampleId;
    private UUID sampleOrderId;
    private UUID sampleUserId;
    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleOrderId = UUID.randomUUID();
        sampleUserId = UUID.randomUUID();

        samplePayment = new Payment();
        samplePayment.setId(sampleId);
        samplePayment.setOrderId(sampleOrderId);
        samplePayment.setUserId(sampleUserId);
        samplePayment.setAmount(new BigDecimal("24.99"));
        samplePayment.setMethod(PaymentMethod.CARD);
        samplePayment.setStatus(PaymentStatus.SUCCESS);
        samplePayment.setTransactionReference("TXN-2026-12345678");
    }

    @Test
    void processPayment_Success() {
        ProcessPaymentRequest req = new ProcessPaymentRequest();
        req.setOrderId(sampleOrderId);
        req.setUserId(sampleUserId);
        req.setAmount(new BigDecimal("24.99"));
        req.setMethod(PaymentMethod.CARD);

        when(paymentRepository.findByOrderId(sampleOrderId)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(sampleId);
            return p;
        });

        PaymentResponse res = paymentService.processPayment(req);

        assertNotNull(res);
        assertEquals(sampleOrderId, res.getOrderId());
        assertEquals(PaymentStatus.SUCCESS, res.getStatus());
        assertTrue(res.getTransactionReference().startsWith("TXN-2026-"));
    }

    @Test
    void processPayment_SimulateFailure() {
        ProcessPaymentRequest req = new ProcessPaymentRequest();
        req.setOrderId(sampleOrderId);
        req.setUserId(sampleUserId);
        req.setAmount(new BigDecimal("24.99"));
        req.setMethod(PaymentMethod.UPI);
        req.setSimulateFailure(true);

        when(paymentRepository.findByOrderId(sampleOrderId)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(sampleId);
            return p;
        });

        PaymentResponse res = paymentService.processPayment(req);

        assertNotNull(res);
        assertEquals(PaymentStatus.FAILED, res.getStatus());
    }

    @Test
    void refundPayment_Success() {
        when(paymentRepository.findById(sampleId)).thenReturn(Optional.of(samplePayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse res = paymentService.refundPayment(sampleId);

        assertEquals(PaymentStatus.REFUNDED, res.getStatus());
    }

    @Test
    void refundPayment_NotSuccess_ThrowsException() {
        samplePayment.setStatus(PaymentStatus.FAILED);
        when(paymentRepository.findById(sampleId)).thenReturn(Optional.of(samplePayment));

        assertThrows(BadRequestException.class, () -> paymentService.refundPayment(sampleId));
    }
}
