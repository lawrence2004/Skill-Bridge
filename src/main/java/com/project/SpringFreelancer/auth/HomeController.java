package com.project.SpringFreelancer.auth;

import com.project.SpringFreelancer.dto.Auth.AuthResponse;
import com.project.SpringFreelancer.dto.Auth.LoginRequest;
import com.project.SpringFreelancer.dto.Auth.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for User Registration and Login")
public class HomeController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register a New User",
            description = "Allows a new user (client or freelancer) to create an account on the platform by providing their email, password, and role details. "
                    + "This endpoint validates the input and returns a JWT token upon successful registration."
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) throws BadRequestException {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @Operation(
            summary = "Login User",
            description = "Allows an existing user to log into the platform by providing valid credentials. "
                    + "Returns a JWT token on successful authentication, which should be used for accessing secured endpoints."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

}
