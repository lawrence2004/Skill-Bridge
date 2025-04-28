package com.project.SpringFreelancer.dto.Bid;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidRequest {

    private Double amount;
    private int timelineInDays;
    private String proposalMessage;
    private Long jobId;

}