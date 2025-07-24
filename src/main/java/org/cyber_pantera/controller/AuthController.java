package org.cyber_pantera.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cyber_pantera.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.cyber_pantera.service.AuthService;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<String>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/confirm")
    public CompletableFuture<ResponseEntity<String>> confirm(@RequestParam String token) {
        return authService.confirmToken(token)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/resend-verification")
    public CompletableFuture<ResponseEntity<String>> resend(@Valid @RequestBody ResendVerificationRequest request) {
        return authService.resendConfirmationEmail(request)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/validate")
    public CompletableFuture<ResponseEntity<UserResponse>> validateToken(@RequestParam("token") String jwtToken) {
        return authService.validateToken(jwtToken)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/validate/{userId}")
    public CompletableFuture<ResponseEntity<UserResponse>> validateUser(@PathVariable long userId) {
        return authService.validateUser(userId)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/forgot-password")
    public CompletableFuture<ResponseEntity<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/reset-password")
    public CompletableFuture<ResponseEntity<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request)
                .thenApply(ResponseEntity::ok);
    }
}
