package com.project.SpringFreelancer.controller;

import com.project.SpringFreelancer.dto.Review.ReviewRequest;
import com.project.SpringFreelancer.dto.Review.ReviewResponse;
import com.project.SpringFreelancer.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Endpoints for managing user reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
            description = "Endpoint for creating a review for a user",
            summary = "Create a review"
    )
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        return ResponseEntity.ok(reviewService.createReview(request, email));
    }

    @Operation(
            description = "Endpoint for fetching all reviews of a specific user",
            summary = "Get reviews for a user"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    private Long extractUserId(UserDetails userDetails) {
        // Implementation depends on your UserDetails implementation
        return Long.parseLong(userDetails.getUsername());
    }
}