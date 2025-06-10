package org.cyber_pantera.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cyber_pantera.dto.AuthRequest;
import org.cyber_pantera.dto.AuthResponse;
import org.cyber_pantera.dto.RegisterRequest;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.entity.VerificationToken;
import org.cyber_pantera.repository.UserRepository;
import org.cyber_pantera.repository.VerificationTokenRepository;
import org.cyber_pantera.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final VerificationTokenRepository tokenRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String register(RegisterRequest request) {

        if (userRepo.findByEmail(request.getEmail()).isPresent())
            throw new RuntimeException("Email already registered");

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(false)
                .build();

        userRepo.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepo.save(verificationToken);

        System.out.println(verificationToken);
        sendConfirmationEmail(user.getEmail(), token);

        return "Registration successful. Please check your email to confirm.";
    }

    public String confirmToken(String token) {
        VerificationToken verificationToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Token expired");

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepo.save(user);
        return "Email confirmed successfully.";
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled())
            throw new RuntimeException("Email not confirmed");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }

    private void sendConfirmationEmail(String toEmail, String token) {
        // TODO write send confirmation request to user email codes
    }
}
