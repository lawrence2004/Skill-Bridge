package com.project.SpringFreelancer.dto.User;

import com.project.SpringFreelancer.model.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {

    private Long id;
    private String name;
    private Role role;

}