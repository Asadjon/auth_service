package org.cyber_pantera.service;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.entity.VerificationToken;
import org.cyber_pantera.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final BytesKeyGenerator tokenGenerator;
    private final VerificationTokenRepository verificationTokenRepo;

    @Value("${verification.token.expiration}")
    private int tokenExpiration;

    public VerificationToken createToken(final User user) {

        verificationTokenRepo.findByUser(user)
                .ifPresent(this::deleteToken);

        String tokenValue = Base64.getEncoder().encodeToString(tokenGenerator.generateKey());

        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(tokenExpiration))
                .build();

        verificationTokenRepo.save(verificationToken);

        return verificationToken;
    }

    public void deleteToken(final VerificationToken token) {
        if (token != null) verificationTokenRepo.delete(token);
    }

    public User checkToken(final String token) {
        VerificationToken verificationToken = verificationTokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Token expired");

        deleteToken(verificationToken);

        return verificationToken.getUser();
    }
}
