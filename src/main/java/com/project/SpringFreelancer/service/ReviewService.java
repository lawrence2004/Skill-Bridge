package com.project.SpringFreelancer.service;

import com.project.SpringFreelancer.dto.Job.JobSummaryDTO;
import com.project.SpringFreelancer.dto.Review.ReviewRequest;
import com.project.SpringFreelancer.dto.Review.ReviewResponse;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.Job;
import com.project.SpringFreelancer.model.JobStatus;
import com.project.SpringFreelancer.model.Review;
import com.project.SpringFreelancer.model.User;
import com.project.SpringFreelancer.repository.JobRepository;
import com.project.SpringFreelancer.repository.ReviewRepository;
import com.project.SpringFreelancer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ReviewResponse createReview(ReviewRequest request, String email) {
        User reviewer = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User reviewedUser = userRepository.findById(request.getReviewedUserId())
                .orElseThrow(() -> new IllegalArgumentException("Reviewed user not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Check if job is completed
        if (job.getStatus() != JobStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot review a job that is not completed");
        }

        // Check if user is part of the job
        boolean isClient = job.getClient().getId().equals(reviewer.getId());
        boolean isFreelancer = job.getHiredFreelancer() != null &&
                job.getHiredFreelancer().getId().equals(reviewer.getId());

        if (!isClient && !isFreelancer) {
            throw new IllegalArgumentException("You are not part of this job");
        }

        // Check if reviewer is not reviewing themselves
        if (reviewer.getId().equals(request.getReviewedUserId())) {
            throw new IllegalArgumentException("You cannot review yourself");
        }

        // Check if reviewer already reviewed the user for this job
        if (reviewRepository.existsByReviewerAndReviewedUserAndJob(reviewer, reviewedUser, job)) {
            throw new IllegalArgumentException("You have already reviewed this user for this job");
        }

        // Validate rating (1-5)
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .reviewer(reviewer)
                .reviewedUser(reviewedUser)
                .job(job)
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToReviewResponse(savedReview);
    }


    public List<ReviewResponse> getUserReviews(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return reviewRepository.findByReviewedUser(user).stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        UserSummaryDTO reviewerDTO = UserSummaryDTO.builder()
                .id(review.getReviewer().getId())
                .name(review.getReviewer().getName())
                .role(review.getReviewer().getRole())
                .build();

        UserSummaryDTO reviewedUserDTO = UserSummaryDTO.builder()
                .id(review.getReviewedUser().getId())
                .name(review.getReviewedUser().getName())
                .role(review.getReviewedUser().getRole())
                .build();

        JobSummaryDTO jobDTO = JobSummaryDTO.builder()
                .id(review.getJob().getId())
                .title(review.getJob().getTitle())
                .budget(review.getJob().getBudget())
                .category(review.getJob().getCategory())
                .build();

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .reviewer(reviewerDTO)
                .reviewedUser(reviewedUserDTO)
                .job(jobDTO)
                .build();
    }
}
