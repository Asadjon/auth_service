package org.cyber_pantera.repository;

import org.cyber_pantera.entity.User;
import org.cyber_pantera.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    @Async
    CompletableFuture<VerificationToken> findByToken(String token);

    @Async
    CompletableFuture<VerificationToken> findByUser(User user);

    void deleteByToken(String token);
}
