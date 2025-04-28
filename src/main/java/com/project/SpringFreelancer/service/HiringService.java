package com.project.SpringFreelancer.service;

import com.project.SpringFreelancer.Exception.ResourceNotFoundException;
import com.project.SpringFreelancer.dto.Job.JobResponse;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.*;
import com.project.SpringFreelancer.repository.BidRepository;
import com.project.SpringFreelancer.repository.JobRepository;
import com.project.SpringFreelancer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HiringService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    public JobResponse hireFreelancer(Long jobId, Long freelancerId, String email) {

        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Verify ownership
        if (!job.getClient().getId().equals(client.getId())) {
            throw new IllegalArgumentException("You don't have permission to hire for this job");
        }

        // Check if job is still in POSTED status
        if (job.getStatus() != JobStatus.POSTED) {
            throw new IllegalArgumentException("Freelancer already hired for this job");
        }

        User freelancer = userRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));

        if (freelancer.getRole() != Role.FREELANCER) {
            throw new IllegalArgumentException("Selected user is not a freelancer");
        }

        // Check if freelancer has placed a bid
        Bid bid = bidRepository.findByFreelancerAndJob(freelancer, job)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer has not placed a bid on this job"));

        // Update job status
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setHiredFreelancer(freelancer);

        // Update bid status
        bid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);

        // Reject all other bids
        bidRepository.rejectOtherBids(job, freelancer);

        Job updatedJob = jobRepository.save(job);

        return mapToJobResponse(updatedJob);
    }

    public JobResponse completeJob(Long jobId, String email) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        // Verify ownership
        if (!job.getClient().getId().equals(client.getId())) {
            throw new IllegalArgumentException("You don't have permission to complete this job");
        }

        // Check if job is in IN_PROGRESS status
        if (job.getStatus() != JobStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Job is not in progress");
        }

        // Update job status
        job.setStatus(JobStatus.COMPLETED);

        Job updatedJob = jobRepository.save(job);

        return mapToJobResponse(updatedJob);
    }

//    public JobResponse submitJob(Long jobId, String email) {
//        Job job = jobRepository.findById(jobId)
//                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
//
//        User freelancer = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("Freelancer not found"));
//
//        // Verify if this freelancer was hired for the job
//        if (job.getHiredFreelancer() == null || !job.getHiredFreelancer().getId().equals(freelancer.getId())) {
//            throw new IllegalArgumentException("You were not hired for this job");
//        }
//
//        // Check if job is in IN_PROGRESS status
//        if (job.getStatus() != JobStatus.IN_PROGRESS) {
//            throw new IllegalArgumentException("Job is not in progress");
//        }
//
//        // We could add a "SUBMITTED" status if needed, or just keep it as IN_PROGRESS
//        // For now, we'll just return the job
//
//        return mapToJobResponse(job);
//    }

    private JobResponse mapToJobResponse(Job job) {
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