package com.tata.self_healing.service;

import com.tata.self_healing.exception.DuplicateEmailException;
import com.tata.self_healing.exception.UserNotFoundException;
import com.tata.self_healing.model.User;
import com.tata.self_healing.repository.JsonFileUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JsonFileUserRepository userRepository;

    public List<User> getAllUsers() {
        logger.info("Retrieving all users");
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        logger.info("Retrieving user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public User createUser(User user) {
        logger.info("Creating new user: {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("User already exists with email: " + user.getEmail());
        }

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        logger.info("Successfully created user: {}", savedUser.getEmail());
        return savedUser;
    }

    public User updateUser(String email, User updatedUser) {
        logger.info("Updating user: {}", email);

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Update fields (email cannot be changed)
        existingUser.setName(updatedUser.getName());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(existingUser);
        logger.info("Successfully updated user: {}", savedUser.getEmail());
        return savedUser;
    }

    public void deleteUser(String email) {
        logger.info("Deleting user: {}", email);

        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        boolean deleted = userRepository.deleteByEmail(email);
        if (deleted) {
            logger.info("Successfully deleted user: {}", email);
        } else {
            logger.error("Failed to delete user: {}", email);
            throw new RuntimeException("Failed to delete user: " + email);
        }
    }

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
