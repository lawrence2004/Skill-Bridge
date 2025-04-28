package com.project.SpringFreelancer.dto.File;

import com.project.SpringFreelancer.dto.Job.JobSummaryDTO;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    private Long id;
    private String fileName;
    private LocalDateTime uploadedAt;
    private UserSummaryDTO uploadedBy;
    private JobSummaryDTO job;

}
