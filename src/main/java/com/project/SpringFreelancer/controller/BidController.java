package com.project.SpringFreelancer.controller;

import com.project.SpringFreelancer.dto.Bid.BidRequest;
import com.project.SpringFreelancer.dto.Bid.BidResponse;
import com.project.SpringFreelancer.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
@Tag(name = "Bids", description = "Endpoints for Bidding on Jobs")
public class BidController {

    private final BidService bidService;

    @Operation(
            description = "Endpoint for freelancers to place a bid on a job",
            summary = "Create a bid for a job"
    )
    @PostMapping
    public ResponseEntity<BidResponse> createBid(@RequestBody BidRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(bidService.createBid(request, email));
    }

    @Operation(
            description = "Endpoint to view all bids placed on a specific job",
            summary = "Get all bids for a job"
    )
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<BidResponse>> getBidsForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(bidService.getBidsForJob(jobId));
    }

    @Operation(
            description = "Endpoint for freelancers to view all the bids they have placed",
            summary = "Get all your bids"
    )
    @GetMapping("/mine")
    public ResponseEntity<List<BidResponse>> getMyBids(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(bidService.getFreelancerBids(email));
    }

}