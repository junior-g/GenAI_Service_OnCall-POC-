package com.tata.self_healing.controller;

import com.tata.self_healing.dto.ApiResponse;
import com.tata.self_healing.model.User;
import com.tata.self_healing.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        logger.info("GET /api/v1/users - Retrieving all users");

        List<User> users = userService.getAllUsers();
        logger.info("Successfully retrieved {} users", users.size());

        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        logger.info("GET /api/v1/users/{} - Retrieving user by email", email);

        User user = userService.getUserByEmail(email);
        logger.info("Successfully retrieved user: {}", email);

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
        logger.info("POST /api/v1/users - Creating user: {}", user.getEmail());

        User createdUser = userService.createUser(user);
        logger.info("Successfully created user: {}", createdUser.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdUser));
    }

    @PutMapping("/{email}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable String email,
            @Valid @RequestBody User user) {
        logger.info("PUT /api/v1/users/{} - Updating user", email);

        User updatedUser = userService.updateUser(email, user);
        logger.info("Successfully updated user: {}", email);

        return ResponseEntity.ok(ApiResponse.success(updatedUser));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String email) {
        logger.info("DELETE /api/v1/users/{} - Deleting user", email);

        userService.deleteUser(email);
        logger.info("Successfully deleted user: {}", email);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success());
    }
}
