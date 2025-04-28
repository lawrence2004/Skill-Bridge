package com.project.SpringFreelancer.dto.Auth;

import com.project.SpringFreelancer.model.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private Role role; // Optional

}
