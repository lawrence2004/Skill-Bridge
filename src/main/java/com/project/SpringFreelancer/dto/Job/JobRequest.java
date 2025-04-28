package com.project.SpringFreelancer.dto.Job;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

    private String title;
    private String description;
    private Double budget;
    private String category;
    private LocalDate deadline;

}