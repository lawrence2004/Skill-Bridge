package com.project.SpringFreelancer.auth;

import com.project.SpringFreelancer.Exception.AdminRegistrationException;
import com.project.SpringFreelancer.Exception.DuplicateEmailException;
import com.project.SpringFreelancer.config.JWTService;
import com.project.SpringFreelancer.dto.Auth.AuthResponse;
import com.project.SpringFreelancer.dto.Auth.LoginRequest;
import com.project.SpringFreelancer.dto.Auth.RegisterRequest;
import com.project.SpringFreelancer.model.Role;
import com.project.SpringFreelancer.model.User;
import com.project.SpringFreelancer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) throws BadRequestException {  

        if(repository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        if(request.getRole() == Role.ADMIN) {
            boolean adminExists = repository.findFirstByRole(Role.ADMIN)
                    .isPresent();

            if (adminExists) {
                throw new AdminRegistrationException("Admin registration restricted");
            }
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(savedUser);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}
