package com.project.SpringFreelancer.dto.Job;

import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.JobStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private Double budget;
    private String category;
    private LocalDate deadline;
    private JobStatus status;
    private LocalDateTime createdAt;
    private UserSummaryDTO client;
    private UserSummaryDTO hiredFreelancer;
    private Integer bidCount;

}
