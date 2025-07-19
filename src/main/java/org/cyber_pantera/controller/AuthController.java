package org.cyber_pantera.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cyber_pantera.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.cyber_pantera.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String token) {
        return ResponseEntity.ok(authService.confirmToken(token));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resend(@Valid @RequestBody ResendVerificationRequest request) {
        return ResponseEntity.ok(authService.resendConfirmationEmail(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<UserResponse> validateToken(@RequestParam("token") String jwtToken) {
        return ResponseEntity.ok(authService.validateToken(jwtToken));
    }

    @GetMapping("/validate/{userId}")
    public ResponseEntity<UserResponse> validateUser(@PathVariable long userId) {
        return ResponseEntity.ok(authService.validateUser(userId));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
