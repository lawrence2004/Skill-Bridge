package com.project.SpringFreelancer.service;

import com.project.SpringFreelancer.dto.Bid.BidRequest;
import com.project.SpringFreelancer.dto.Bid.BidResponse;
import com.project.SpringFreelancer.dto.Job.JobSummaryDTO;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.*;
import com.project.SpringFreelancer.repository.BidRepository;
import com.project.SpringFreelancer.repository.JobRepository;
import com.project.SpringFreelancer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public BidResponse createBid(BidRequest request, String email) {
        User freelancer = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (freelancer.getRole() != Role.FREELANCER) {
            throw new IllegalArgumentException("Only freelancers can place bids");
        }

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Check if job is still in POSTED status
        if (job.getStatus() != JobStatus.POSTED) {
            throw new IllegalArgumentException("Cannot place bid on job that is already in progress or completed");
        }

        // Check if freelancer already placed a bid on this job
        if (bidRepository.existsByFreelancerAndJob(freelancer, job)) {
            throw new IllegalArgumentException("You have already placed a bid on this job");
        }

        Bid bid = Bid.builder()
                .amount(request.getAmount())
                .timelineInDays(request.getTimelineInDays())
                .proposalMessage(request.getProposalMessage())
                .freelancer(freelancer)
                .job(job)
                .status(BidStatus.PENDING)
                .build();

        Bid savedBid = bidRepository.save(bid);
        return mapToBidResponse(savedBid);
    }

    public List<BidResponse> getBidsForJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        return bidRepository.findByJob(job).stream()
                .map(this::mapToBidResponse)
                .collect(Collectors.toList());
    }

    public List<BidResponse> getFreelancerBids(String email) {
        User freelancer = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return bidRepository.findByFreelancer(freelancer).stream()
                .map(this::mapToBidResponse)
                .collect(Collectors.toList());
    }

    private BidResponse mapToBidResponse(Bid bid) {
        UserSummaryDTO freelancerDTO = UserSummaryDTO.builder()
                .id(bid.getFreelancer().getId())
                .name(bid.getFreelancer().getName())
                .role(bid.getFreelancer().getRole())
                .build();

        JobSummaryDTO jobDTO = JobSummaryDTO.builder()
                .id(bid.getJob().getId())
                .title(bid.getJob().getTitle())
                .budget(bid.getJob().getBudget())
                .category(bid.getJob().getCategory())
                .build();

        return BidResponse.builder()
                .id(bid.getId())
                .amount(bid.getAmount())
                .timelineInDays(bid.getTimelineInDays())
                .proposalMessage(bid.getProposalMessage())
                .status(bid.getStatus())
                .createdAt(bid.getCreatedAt())
                .freelancer(freelancerDTO)
                .job(jobDTO)
                .build();
    }
}