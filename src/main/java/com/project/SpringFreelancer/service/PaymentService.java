package com.project.SpringFreelancer.service;

import com.project.SpringFreelancer.Exception.ResourceNotFoundException;
import com.project.SpringFreelancer.dto.Job.JobSummaryDTO;
import com.project.SpringFreelancer.dto.Payment.PaymentResponse;
import com.project.SpringFreelancer.dto.Review.ReviewRequest;
import com.project.SpringFreelancer.dto.Review.ReviewResponse;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.*;
import com.project.SpringFreelancer.repository.JobRepository;
import com.project.SpringFreelancer.repository.PaymentRepository;
import com.project.SpringFreelancer.repository.ReviewRepository;
import com.project.SpringFreelancer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public PaymentResponse initiatePayment(Long jobId, String email) {

        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if(!job.getClient().getId().equals(jobId)) {
            throw new IllegalArgumentException("You don't have permission to initiate payment for this job");
        }

        if(job.getStatus() != JobStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot initiate payment for a job that is not completed");
        }

        if(paymentRepository.existsByJob(job)) {
            throw new IllegalArgumentException("Payment already initiated for this job");
        }

        User freelancer = job.getHiredFreelancer();
        if(freelancer == null) {
            throw new IllegalArgumentException("No freelancer is hired for this job");
        }

        Payment payment = Payment.builder()
                .amount(job.getBudget())
                .status(PaymentStatus.PENDING)
                .job(job)
                .payer(client)
                .payee(freelancer)
                .build();

        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);

        return mapToPaymentResponse(payment);

    }

    public PaymentResponse getPaymentStatus(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        Payment payment = paymentRepository.findByJob(job)
                .orElseThrow(() -> new IllegalArgumentException("No payment found for this job"));

        return mapToPaymentResponse(payment);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {

        UserSummaryDTO payee = UserSummaryDTO.builder()
                .id(payment.getPayee().getId())
                .name(payment.getPayee().getName())
                .role(payment.getPayee().getRole())
                .build();

        UserSummaryDTO payer = UserSummaryDTO.builder()
                .id(payment.getPayer().getId())
                .name(payment.getPayer().getName())
                .role(payment.getPayer().getRole())
                .build();

        JobSummaryDTO job = JobSummaryDTO.builder()
                .id(payment.getJob().getId())
                .title(payment.getJob().getTitle())
                .budget(payment.getJob().getBudget())
                .category(payment.getJob().getCategory())
                .build();

        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .payer(payer)
                .payee(payee)
                .createdAt(payment.getCreatedAt())
                .job(job)
                .build();
    }
}
