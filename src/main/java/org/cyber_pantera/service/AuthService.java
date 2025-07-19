package org.cyber_pantera.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cyber_pantera.dto.*;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.exception.EmailConfirmationException;
import org.cyber_pantera.exception.InvalidCredentialsException;
import org.cyber_pantera.exception.UserNotFoundException;
import org.cyber_pantera.mailing.AccountVerificationEmailContext;
import org.cyber_pantera.mailing.EmailService;
import org.cyber_pantera.mailing.ForgotPasswordEmailContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private final BalanceService balanceService;

    @Value("${base.url}")
    private String baseUrl;

    @Transactional
    public String register(RegisterRequest request) {
        var user = User.builder()
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
        var user = verificationTokenService.checkToken(token);
        user.setEnabled(true);
        userService.update(user);
        balanceService.initUserBalance(user);
        return "Email confirmed successfully";
    }

    public AuthResponse login(AuthRequest request) {
        var user = userService.getUserByEmail(request.getEmail());

        if (!user.isEnabled())
            throw new EmailConfirmationException("Email not confirmed");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException(new HashSet<>(List.of("Incorrect password")));

        var token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }

    private void sendConfirmationEmail(User user) {
        var verificationToken = verificationTokenService.createToken(user);
        var context = new AccountVerificationEmailContext();
        context.init(user);
        context.setToken(verificationToken.getToken());
        context.buildVerificationUrl(baseUrl, verificationToken.getToken());

        emailService.sendMail(context);
    }

    public String resendConfirmationEmail(ResendVerificationRequest request) {
        var user = userService.getUserByEmail(request.getEmail());

        if (user.isEnabled())
            throw new EmailConfirmationException("Email already verified");

        sendConfirmationEmail(user);

        return "Confirmation email has been resent";
    }

    public UserResponse validateToken(String jwtToken) {
        var email = jwtService.extractUsername(jwtToken);
        var user = userService.getUserByEmail(email);

        if (!user.isEnabled())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");

        return mapToUserResponse(user);
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        var user = userService.getUserByEmail(request.getEmail());

        if (!user.isEnabled())
            throw new EmailConfirmationException("Email not confirmed");

        var verificationToken = verificationTokenService.createToken(user);
        var context = new ForgotPasswordEmailContext();
        context.init(user);
        context.setToken(verificationToken.getToken());
        context.buildVerificationUrl(baseUrl, verificationToken.getToken());

        emailService.sendMail(context);

        return "Password reset link sent to your email";
    }

    public String resetPassword(ResetPasswordRequest request) {
        var user = verificationTokenService.checkToken(request.getToken());

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.update(user);

        return "Password successfully reset";
    }

    public UserResponse validateUser(long userId) {
        var user = userService.getUserById(userId);

        if (!user.isEnabled())
            throw new UserNotFoundException("User not found");

        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
