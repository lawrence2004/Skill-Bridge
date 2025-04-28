package com.project.SpringFreelancer.dto.Auth;

import com.project.SpringFreelancer.model.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

}