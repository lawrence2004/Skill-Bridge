package com.project.SpringFreelancer.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    private Long reviewedUserId;
    private Long jobId;
    private int rating;
    private String comment;

}