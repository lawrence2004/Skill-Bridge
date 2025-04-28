package com.project.SpringFreelancer.controller;

import com.project.SpringFreelancer.Exception.ResourceNotFoundException;
import com.project.SpringFreelancer.config.JWTService;
import com.project.SpringFreelancer.dto.Job.JobRequest;
import com.project.SpringFreelancer.dto.Job.JobResponse;
import com.project.SpringFreelancer.model.Job;
import com.project.SpringFreelancer.model.JobStatus;
import com.project.SpringFreelancer.model.User;
import com.project.SpringFreelancer.repository.UserRepository;
import com.project.SpringFreelancer.service.JobService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs Management", description = "Endpoints for managing jobs")
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;

    @Operation(
            description = "Endpoint for creating a new job by a CLIENT",
            summary = "Create a new job (Only CLIENT role allowed)"
    )
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<JobResponse> createJob(@RequestBody JobRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {

        String email = extractUserEmail(userDetails);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(jobService.createJob(request, user));

    }

    @Operation(
            description = "Endpoint to search jobs using filters like title, category, budget, deadline, and status",
            summary = "Search jobs with multiple filters"
    )
    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minBudget,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadlineBefore,
            @RequestParam(required = false) JobStatus status
    ) {
        return ResponseEntity.ok(jobService.searchJobs(title, category, minBudget, maxBudget, deadlineBefore, status));
    }

    @Operation(
            description = "Endpoint to search jobs by matching title or category",
            summary = "Search jobs by title or category"
    )
    @GetMapping("/search/title")
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam String searchTerm
    ) {
        return ResponseEntity.ok(jobService.searchJobsWithTitleOrCategory(searchTerm));
    }

    @Operation(
            description = "Endpoint to fetch all available jobs",
            summary = "Get all jobs"
    )
    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @Operation(
            description = "Endpoint for a CLIENT to view jobs they have posted",
            summary = "Get logged-in client's jobs"
    )
    @GetMapping("/mine")
    public ResponseEntity<List<JobResponse>> getMyJobs(@AuthenticationPrincipal UserDetails userDetails) {
        String email = extractUserEmail(userDetails);
        return ResponseEntity.ok(jobService.getClientJobs(email));
    }

    @Operation(
            description = "Endpoint to fetch a specific job by its ID",
            summary = "Get job by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @Operation(
            description = "Endpoint for a CLIENT to update their own job",
            summary = "Update a job (Only Owner CLIENT allowed)"
    )
    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id,
                                                 @RequestBody JobRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String email = extractUserEmail(userDetails);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(jobService.updateJob(id, request, user.getId()));
    }

    @Operation(
            description = "Endpoint for a CLIENT to delete their own job",
            summary = "Delete a job (Only Owner CLIENT allowed)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        String email = extractUserEmail(userDetails);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        jobService.deleteJob(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    private String extractUserEmail(UserDetails userDetails) {
        // Implementation depends on your UserDetails implementation
        return userDetails.getUsername();
    }
}