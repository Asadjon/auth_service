package org.cyber_pantera.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.entity.VerificationToken;
import org.cyber_pantera.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepo;
    private final JwtService jwtService;

    public VerificationToken createToken(final User user) {
        var token = jwtService.generateToken(user);

        var verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .build();

        return verificationTokenRepo.save(verificationToken);
    }

    @Transactional
    public void deleteToken(final String token) {
        if (token != null && !token.isEmpty())
            verificationTokenRepo.deleteByToken(token);
    }

    @Transactional
    public CompletableFuture<VerificationToken> validateToken(final String token) {
        if (jwtService.isTokenExpired(token))
            return CompletableFuture.completedFuture(null);

        var extractedEmail = jwtService.extractUsername(token);

        if (extractedEmail == null || extractedEmail.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return verificationTokenRepo.findByToken(token)
                .thenApply(verificationToken ->
                        verificationToken != null && extractedEmail.equals(verificationToken.getUser().getEmail())
                                ? verificationToken
                                : null);
    }
}
