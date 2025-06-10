package org.cyber_pantera.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cyber_pantera.dto.AuthRequest;
import org.cyber_pantera.dto.AuthResponse;
import org.cyber_pantera.dto.RegisterRequest;
import org.cyber_pantera.dto.ResendVerificationRequest;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.entity.VerificationToken;
import org.cyber_pantera.mailing.AccountVerificationEmailContext;
import org.cyber_pantera.mailing.EmailService;
import org.cyber_pantera.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @Value("${base.url}")
    private String baseUrl;

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

        sendConfirmationEmail(user);

        return "Registration successful. Please check your email to confirm.";
    }

    public String confirmToken(String token) {
        User user = verificationTokenService.checkToken(token);
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

    private void sendConfirmationEmail(User user) {
        VerificationToken verificationToken = verificationTokenService.createToken(user);
        AccountVerificationEmailContext context = new AccountVerificationEmailContext();
        context.init(user);
        context.setToken(verificationToken.getToken());
        context.buildVerificationUrl(baseUrl, verificationToken.getToken());

        try {
            emailService.sendMail(context);
        } catch (MessagingException e) {
            log.error("Error sending email:{}", e.getMessage());
            throw new RuntimeException("Email not sent", e);
        }
    }

    public String resendConfirmationEmail(ResendVerificationRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled())
            throw new RuntimeException("Email already registered");

        sendConfirmationEmail(user);

        return "Confirmation email has been resent";
    }
}
