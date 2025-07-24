package org.cyber_pantera.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cyber_pantera.dto.*;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.exception.EmailConfirmationException;
import org.cyber_pantera.exception.InvalidCredentialsException;
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
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<String> register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(false)
                .build();

        return userService.addNewUser(user)
                .thenCompose(savedUser ->
                        sendConfirmationEmail(savedUser)
                                .thenApply(emailResult -> "Registration successful. Please check your email to confirm.")
                                .exceptionally(ex -> "Registration successful, but failed to send confirmation email: " + ex.getMessage())
                );
    }

    @Transactional
    public CompletableFuture<String> confirmToken(String token) {


        return verificationTokenService.validateToken(token)
                .thenApply(verificationToken -> {
                    if (verificationToken == null)
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

                    verificationTokenService.deleteToken(token);
                    return verificationToken.getUser();
                })

                .thenCompose(user -> {
                    user.setEnabled(true);
                    return userService.update(user); })
                .thenCompose(balanceService::initUserBalance)
                .thenApply(unused -> "Email confirmed successfully");
    }

    public CompletableFuture<AuthResponse> login(AuthRequest request) {
        return userService.getUserByEmail(request.getEmail())
                .thenApply(user -> {
                    if (!user.isEnabled())
                        throw new EmailConfirmationException("Email not confirmed");

                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
                        throw new InvalidCredentialsException(new HashSet<>(List.of("Incorrect password")));

                    var token = jwtService.generateToken(user);
                    return new AuthResponse(token, user.getEmail(), user.getRole());
                });
    }

    private CompletableFuture<Void> sendConfirmationEmail(User user) {
        var verificationToken = verificationTokenService.createToken(user);
        var context = new AccountVerificationEmailContext();
        context.init(user);
        context.setToken(verificationToken.getToken());
        context.buildVerificationUrl(baseUrl, verificationToken.getToken());

        return CompletableFuture.runAsync(() -> emailService.sendMail(context))
                .exceptionally(ex -> {
                    log.error("Failed to send verification email", ex);
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send verification email");
                });
    }

    public CompletableFuture<String> resendConfirmationEmail(ResendVerificationRequest request) {
        return userService.getUserByEmail(request.getEmail())
                .thenCompose(user -> {

                    if (user.isEnabled())
                        throw new EmailConfirmationException("Email already verified");

                    return sendConfirmationEmail(user)
                            .thenApply(emailResult -> "Confirmation email has been resent");
                });
    }

    public CompletableFuture<UserResponse> validateToken(String jwtToken) {
        var email = jwtService.extractUsername(jwtToken);
        return userService.getUserByEmail(email)
                .thenApply(user -> {

                    if (!user.isEnabled())
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");

                    return mapToUserResponse(user);
                });
    }

    public CompletableFuture<String> forgotPassword(ForgotPasswordRequest request) {
        return userService.getUserByEmail(request.getEmail())
                .thenCompose(user -> {
                    if (!user.isEnabled())
                        throw new EmailConfirmationException("Email not confirmed");

                    return sendPasswordResetEmail(user)
                            .thenApply(unused -> "Password reset link sent to your email");
                });
    }

    private CompletableFuture<Void> sendPasswordResetEmail(User user) {
        var verificationToken = verificationTokenService.createToken(user);
        var context = new ForgotPasswordEmailContext();
        context.init(user);
        context.setToken(verificationToken.getToken());
        context.buildVerificationUrl(baseUrl, verificationToken.getToken());

        return CompletableFuture.runAsync(() -> emailService.sendMail(context));
    }

    public CompletableFuture<String> resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match");

        return verificationTokenService.validateToken(request.getToken())
                .thenApply(verificationToken -> {
                    if (verificationToken == null)
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

                    verificationTokenService.deleteToken(verificationToken.getToken());
                    return verificationToken.getUser();
                })

                .thenCompose(user -> {
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    return userService.update(user);
                })
                .thenApply(user -> "Password successfully reset");
    }

    public CompletableFuture<UserResponse> validateUser(long userId) {
        return userService.getUserById(userId)
                .thenApply(user -> {
                    if (!user.isEnabled())
                        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User is not enabled");

                    return mapToUserResponse(user);
                });
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
