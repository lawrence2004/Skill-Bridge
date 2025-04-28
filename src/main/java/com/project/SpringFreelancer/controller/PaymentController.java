package com.project.SpringFreelancer.controller;

import com.project.SpringFreelancer.dto.Payment.PaymentResponse;
import com.project.SpringFreelancer.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Endpoints for handling job payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            description = "Endpoint to initiate a payment for a job",
            summary = "Initiate job payment"
    )
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestParam Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        return ResponseEntity.ok(paymentService.initiatePayment(jobId, email));
    }

    @Operation(
            description = "Endpoint to check payment status for a specific job",
            summary = "Check payment status"
    )
    @GetMapping("/status/{jobId}")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable Long jobId) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(jobId));
    }

    private Long extractUserId(UserDetails userDetails) {
        // Implementation depends on your UserDetails implementation
        return Long.parseLong(userDetails.getUsername());
    }
}