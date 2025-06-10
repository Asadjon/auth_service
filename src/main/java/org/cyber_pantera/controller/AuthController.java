package org.cyber_pantera.controller;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.dto.AuthRequest;
import org.cyber_pantera.dto.AuthResponse;
import org.cyber_pantera.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.cyber_pantera.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String token) {
        return ResponseEntity.ok(authService.confirmToken(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
