package org.cyber_pantera.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.exception.EmailAlreadyExistsException;
import org.cyber_pantera.exception.UserNotFoundException;
import org.cyber_pantera.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    @Transactional
    public CompletableFuture<User> getUserById(long id) {

        return exceptionally(userRepo.findById(id))
                .thenApply(Optional::ofNullable)
                .thenApply(userOptional ->
                        userOptional.orElseThrow(() -> new UserNotFoundException("id: " + id)));
    }

    @Transactional
    public CompletableFuture<User> getUserByEmail(String email) {
        return exceptionally(userRepo.findByEmail(email))
                .thenApply(Optional::ofNullable)
                .thenApply(user ->
                        user.orElseThrow(() -> new UserNotFoundException(email)));
    }

    @Transactional
    public CompletableFuture<User> addNewUser(User newUser) {
        return exceptionally(userRepo.findByEmail(newUser.getEmail()))
                .thenApply(user -> {
                    if (user != null)
                        throw new EmailAlreadyExistsException(user.getEmail());

                    return userRepo.save(newUser);
                });
    }

    @Transactional
    public CompletableFuture<User> update(User user) {
        return CompletableFuture.anyOf(
                exceptionally(userRepo.findByEmail(user.getEmail())),
                exceptionally(userRepo.findById(user.getId())))
                .thenApply(o -> {
                    if (o instanceof User)
                        return userRepo.save((User) o);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
                });
    }

    private CompletableFuture<User> exceptionally(CompletableFuture<User> future) {
        return  future.exceptionally(e -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        });
    }
}
