package com.project.SpringFreelancer.dto.Bid;

import com.project.SpringFreelancer.dto.Job.JobSummaryDTO;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.BidStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {

    private Long id;
    private Double amount;
    private int timelineInDays;
    private String proposalMessage;
    private BidStatus status;
    private LocalDateTime createdAt;
    private UserSummaryDTO freelancer;
    private JobSummaryDTO job;

}