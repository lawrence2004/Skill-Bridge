package com.project.SpringFreelancer.dto.Review;

import com.project.SpringFreelancer.dto.Job.JobSummaryDTO;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.Job;
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
public class ReviewResponse {

    private Long id;
    private int rating;
    private String comment;
    private UserSummaryDTO reviewer;
    private UserSummaryDTO reviewedUser;
    private JobSummaryDTO job;
    private LocalDateTime createdAt;

}
