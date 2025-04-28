package com.project.SpringFreelancer.controller;

import com.project.SpringFreelancer.Exception.ResourceNotFoundException;
import com.project.SpringFreelancer.config.JWTService;
import com.project.SpringFreelancer.dto.User.UserProfileRequest;
import com.project.SpringFreelancer.dto.User.UserProfileResponse;
import com.project.SpringFreelancer.model.User;
import com.project.SpringFreelancer.repository.UserRepository;
import com.project.SpringFreelancer.service.UserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.Resolution;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for User Management and Profile Updates")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(
            description = "Endpoint for ADMIN to fetch all registered users",
            summary = "Get all users (ADMIN only)"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllJobs());
    }

    @Operation(
            description = "Endpoint for logged-in users to fetch their own profile details",
            summary = "Get your profile"
    )
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = extractUserEmail(userDetails);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @Operation(
            description = "Endpoint for fetching another user's profile by their user ID",
            summary = "Get a user profile by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @Operation(
            description = "Endpoint for logged-in users to update their profile information",
            summary = "Update your profile"
    )
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = extractUserEmail(userDetails);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(userService.updateUserProfile(request, user.getId()));
    }

    private String extractUserEmail(UserDetails userDetails) {
        // Implementation depends on your UserDetails implementation
        return userDetails.getUsername();
    }

}