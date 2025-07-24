package org.cyber_pantera.repository;

import org.cyber_pantera.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserRepository extends JpaRepository<User, Long> {

    @Async
    CompletableFuture<User> findByEmail(String email);

    @Async
    CompletableFuture<User> findById(long id);
}
