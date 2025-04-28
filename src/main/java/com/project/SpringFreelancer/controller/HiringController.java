package com.project.SpringFreelancer.controller;

import com.project.SpringFreelancer.dto.Job.JobResponse;
import com.project.SpringFreelancer.model.User;
import com.project.SpringFreelancer.service.HiringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Hiring Management", description = "Endpoints for Hiring and Managing Job Completion")
public class HiringController {

    private final HiringService hiringService;

    @Operation(
            description = "Endpoint to hire a freelancer for a specific job by a client",
            summary = "Hire a freelancer for a job (CLIENT only)"
    )
    @PostMapping("/{jobId}/hire/{freelancerId}")
    public ResponseEntity<JobResponse> hireFreelancer(
            @PathVariable Long jobId,
            @PathVariable Long freelancerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        return ResponseEntity.ok(hiringService.hireFreelancer(jobId, freelancerId, email));
    }

    @Operation(
            description = "Endpoint for a client to mark a job as completed after freelancer finishes the work",
            summary = "Complete a job (CLIENT only)"
    )
    @PutMapping("/{jobId}/complete")
    public ResponseEntity<JobResponse> completeJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        return ResponseEntity.ok(hiringService.completeJob(jobId, email));
    }

//    @Operation(
//            description = "End point for submitting all Job",
//            summary = ""
//    )
//    @PutMapping("/{jobId}/submit")
//    public ResponseEntity<JobResponse> submitJob(
//            @PathVariable Long jobId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        String email = userDetails.getUsername();
//        return ResponseEntity.ok(hiringService.submitJob(jobId, email));
//    }

//    private Long extractUserId(UserDetails userDetails) {
//        // Implementation depends on your UserDetails implementation
//        return Long.parseLong(userDetails.getUsername());
//    }
}
