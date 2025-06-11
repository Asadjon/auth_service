package org.cyber_pantera.service;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.entity.VerificationToken;
import org.cyber_pantera.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepo;
    private final JwtService jwtService;

    @Value("${verification.token.expiration}")
    private int tokenExpiration;

    public VerificationToken createToken(final User user) {
        String token = jwtService.generateToken(user);

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
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

        jwtService.extractUsername(verificationToken.getToken());

        deleteToken(verificationToken);

        return verificationToken.getUser();
    }
}
