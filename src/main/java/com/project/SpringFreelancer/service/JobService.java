package com.project.SpringFreelancer.service;

import com.project.SpringFreelancer.dto.Job.JobRequest;
import com.project.SpringFreelancer.dto.Job.JobResponse;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.Job;
import com.project.SpringFreelancer.model.JobStatus;
import com.project.SpringFreelancer.model.Role;
import com.project.SpringFreelancer.model.User;
import com.project.SpringFreelancer.repository.JobRepository;
import com.project.SpringFreelancer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobResponse createJob(JobRequest request, User user) {

        if (user.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("Only clients can post jobs");
        }

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .budget(request.getBudget())
                .deadline(request.getDeadline())
                .category(request.getCategory())
                .client(user)
                .status(JobStatus.POSTED)
                .build();

        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> getClientJobs(String email) {
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return jobRepository.findByClient(client)
                .stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());
    }

    public JobResponse getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        return mapToJobResponse(job);
    }

    public JobResponse updateJob(Long jobId, JobRequest request, Long clientId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Verify ownership
        if (!job.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException("You don't have permission to update this job");
        }

        // Only allow updates if job is still in POSTED status
        if (job.getStatus() != JobStatus.POSTED) {
            throw new IllegalArgumentException("Cannot update job that is already in progress or completed");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setBudget(request.getBudget());
        job.setDeadline(request.getDeadline());
        job.setCategory(request.getCategory());

        Job updatedJob = jobRepository.save(job);
        return mapToJobResponse(updatedJob);
    }

    public void deleteJob(Long jobId, Long clientId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Verify ownership
        if (!job.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException("You don't have permission to delete this job");
        }

        // Only allow deletion if job is still in POSTED status
        if (job.getStatus() != JobStatus.POSTED) {
            throw new IllegalArgumentException("Cannot delete job that is already in progress or completed");
        }

        jobRepository.delete(job);
    }

    public List<JobResponse> searchJobs(String title, String category, Double minBudget, Double maxBudget,
                                LocalDate deadlineBefore, JobStatus status) {
        List<Job> jobs =  jobRepository.searchJobs(title, category, minBudget, maxBudget, deadlineBefore, status);
        return jobs.stream()
                .map(this::mapToJobResponse)
                .toList();
//        return responses;
    }

    public List<JobResponse> searchJobsWithTitleOrCategory(String searchTerm) {
        String normalizedSearchTerm = searchTerm.toLowerCase();
        List<Job> jobs = jobRepository.findByTitleOrCategoryContaining(normalizedSearchTerm);
        return jobs.stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());
    }

    public JobResponse mapToJobResponse(Job job) {
        UserSummaryDTO clientDTO = UserSummaryDTO.builder()
                .id(job.getClient().getId())
                .name(job.getClient().getName())
                .role(job.getClient().getRole())
                .build();

        UserSummaryDTO freelancerDTO = null;
        if (job.getHiredFreelancer() != null) {
            freelancerDTO = UserSummaryDTO.builder()
                    .id(job.getHiredFreelancer().getId())
                    .name(job.getHiredFreelancer().getName())
                    .role(job.getHiredFreelancer().getRole())
                    .build();
        }

        int bidCount = job.getBids() != null ? job.getBids().size() : 0;

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .budget(job.getBudget())
                .deadline(job.getDeadline())
                .category(job.getCategory())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .client(clientDTO)
                .hiredFreelancer(freelancerDTO)
                .bidCount(bidCount)
                .build();
    }
}