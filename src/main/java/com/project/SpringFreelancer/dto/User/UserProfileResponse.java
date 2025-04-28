package com.project.SpringFreelancer.dto.User;

import com.project.SpringFreelancer.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String profileDescription;
    private String profileImageUrl;
    private LocalDateTime createdAt;

}