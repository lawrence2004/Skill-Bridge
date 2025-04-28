package com.project.SpringFreelancer.dto.Job;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSummaryDTO {

    private Long id;
    private String title;
    private Double budget;
    private String category;

}