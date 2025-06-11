package org.cyber_pantera.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cyber_pantera.dto.*;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.entity.VerificationToken;
import org.cyber_pantera.exception.EmailConfirmationException;
import org.cyber_pantera.exception.InvalidCredentialsException;
import org.cyber_pantera.mailing.AccountVerificationEmailContext;
import org.cyber_pantera.mailing.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @Value("${base.url}")
    private String baseUrl;

    @Transactional
    public String register(RegisterRequest request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(false)
                .build();

        userService.addNewUser(user);

        sendConfirmationEmail(user);

        return "Registration successful. Please check your email to confirm";
    }

    public String confirmToken(String token) {
        User user = verificationTokenService.checkToken(token);
        user.setEnabled(true);
        userService.update(user);
        return "Email confirmed successfully";
    }

    public AuthResponse login(AuthRequest request) {
        User user = userService.getUserByEmail(request.getEmail());

        if (!user.isEnabled())
            throw new EmailConfirmationException("Email not confirmed");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException(new HashSet<>(List.of("Incorrect password")));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }

    private void sendConfirmationEmail(User user) {
        VerificationToken verificationToken = verificationTokenService.createToken(user);
        AccountVerificationEmailContext context = new AccountVerificationEmailContext();
        context.init(user);
        context.setToken(verificationToken.getToken());
        context.buildVerificationUrl(baseUrl, verificationToken.getToken());

        emailService.sendMail(context);
    }

    public String resendConfirmationEmail(ResendVerificationRequest request) {
        User user = userService.getUserByEmail(request.getEmail());

        if (user.isEnabled())
            throw new EmailConfirmationException("Email already verified");

        sendConfirmationEmail(user);

        return "Confirmation email has been resent";
    }

    public ValidationTokenResponse validateToken(String jwtToken) {
        var email = jwtService.extractUsername(jwtToken);
        var user = userService.getUserByEmail(email);

        if (!user.isEnabled())
            throw new EmailConfirmationException("Email not confirmed");

        return new ValidationTokenResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
