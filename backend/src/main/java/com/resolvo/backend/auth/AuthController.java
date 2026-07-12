package com.resolvo.backend.auth;

import com.resolvo.backend.auth.dto.AuthResponse;
import com.resolvo.backend.auth.dto.LoginRequest;
import com.resolvo.backend.auth.dto.RegisterRequest;
import com.resolvo.backend.common.constants.ApiPaths;
import com.resolvo.backend.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ApiPaths.Auth.BASE)
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration and login - the only public endpoints in the API")
public class AuthController {

    private final AuthService authService;

    @PostMapping(ApiPaths.Auth.REGISTER)
    @Operation(summary = "Register a new account",
            description = "Creates a RESIDENT or ADMIN account and returns a JWT immediately - no separate login required after registering.")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for email={}, role={}", request.getEmail(), request.getRole());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping(ApiPaths.Auth.LOGIN)
    @Operation(summary = "Log in", description = "Returns a JWT valid for resolvo.jwt.expiration-ms (default 24h). Use it as a Bearer token on every other endpoint.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email={}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}