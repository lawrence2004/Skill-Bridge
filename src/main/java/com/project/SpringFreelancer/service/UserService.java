package com.project.SpringFreelancer.service;

import com.project.SpringFreelancer.dto.User.UserProfileRequest;
import com.project.SpringFreelancer.dto.User.UserProfileResponse;
import com.project.SpringFreelancer.model.User;
import com.project.SpringFreelancer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserProfileResponse> getAllJobs() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserProfileResponse)
                .collect(Collectors.toList());
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mapToUserProfileResponse(user);
    }

    public UserProfileResponse updateUserProfile(UserProfileRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setName(request.getName());
        user.setProfileDescription(request.getProfileDescription());

        User updatedUser = userRepository.save(user);
        return mapToUserProfileResponse(updatedUser);
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .profileDescription(user.getProfileDescription())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}