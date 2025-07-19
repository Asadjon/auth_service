package org.cyber_pantera.service;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.entity.User;
import org.cyber_pantera.exception.EmailAlreadyExistsException;
import org.cyber_pantera.exception.UserNotFoundException;
import org.cyber_pantera.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public User getUserById(long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void addNewUser(User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent())
            throw new EmailAlreadyExistsException(user.getEmail());

        userRepo.save(user);
    }

    public void update(User user) {
        if (userRepo.findByEmail(user.getEmail())
                .or(() -> userRepo.findById(user.getId()))
                .isEmpty())
            throw new UserNotFoundException(user.getEmail());

        userRepo.save(user);
    }
}
