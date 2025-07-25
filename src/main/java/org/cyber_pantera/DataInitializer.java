package org.cyber_pantera;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.cyber_pantera.dto.RegisterRequest;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.service.BalanceService;
import org.cyber_pantera.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final BalanceService balanceService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try (var inputStream = getClass().getResourceAsStream("/users.json")) {
            if (inputStream != null) {
                objectMapper.readValue(inputStream, new TypeReference<List<RegisterRequest>>() {
                        })
                        .stream()
                        .map(this::mapToUser)
                        .map(user -> userService.addNewUser(user)
                                .thenCompose(balanceService::initUserBalance))
                        .forEach(CompletableFuture::join);
                return;
            }

            throw new FileNotFoundException("users.json not found");
        }
    }

    private User mapToUser(RegisterRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();
    }
}
