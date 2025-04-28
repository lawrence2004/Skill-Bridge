package com.project.SpringFreelancer.dto.Payment;

import com.project.SpringFreelancer.dto.Job.JobSummaryDTO;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.Job;
import com.project.SpringFreelancer.model.PaymentStatus;
import com.project.SpringFreelancer.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Double amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private UserSummaryDTO payer;
    private UserSummaryDTO payee;
    private JobSummaryDTO job;

}