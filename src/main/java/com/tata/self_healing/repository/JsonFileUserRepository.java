package com.tata.self_healing.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tata.self_healing.exception.FileOperationException;
import com.tata.self_healing.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JsonFileUserRepository {
    private static final Logger logger = LoggerFactory.getLogger(JsonFileUserRepository.class);
    private static final String DATA_DIR = "./data";
    private static final String USERS_FILE = DATA_DIR + "/users.json";
    private static final String TEMP_FILE = DATA_DIR + "/users.json.tmp";

    private final ObjectMapper objectMapper;

    public JsonFileUserRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        initializeDataDirectory();
    }

    private void initializeDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                logger.info("Created data directory: {}", DATA_DIR);
            }

            Path usersFile = Paths.get(USERS_FILE);
            if (!Files.exists(usersFile)) {
                Files.write(usersFile, "[]".getBytes());
                logger.info("Created users file: {}", USERS_FILE);
            }
        } catch (IOException e) {
            logger.error("Failed to initialize data directory", e);
            throw new FileOperationException("Failed to initialize data directory", e);
        }
    }

    public List<User> findAll() {
        return readUsersFromFile();
    }

    public Optional<User> findByEmail(String email) {
        List<User> users = readUsersFromFile();
        return users.stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    public User save(User user) {
        List<User> users = readUsersFromFile();

        // Check for duplicate email (except when updating same user)
        boolean emailExists = users.stream()
                .anyMatch(existingUser -> user.getEmail().equals(existingUser.getEmail()));

        if (emailExists) {
            // Remove existing user for update
            users.removeIf(existingUser -> user.getEmail().equals(existingUser.getEmail()));
        }

        users.add(user);
        writeUsersToFile(users);

        logger.info("Saved user: {}", user.getEmail());
        return user;
    }

    public boolean deleteByEmail(String email) {
        List<User> users = readUsersFromFile();
        boolean removed = users.removeIf(user -> email.equals(user.getEmail()));

        if (removed) {
            writeUsersToFile(users);
            logger.info("Deleted user: {}", email);
        }

        return removed;
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    private List<User> readUsersFromFile() {
        Path filePath = Paths.get(USERS_FILE);

        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            try (FileLock lock = channel.tryLock(0, Long.MAX_VALUE, true)) { // Shared lock for reading
                if (lock == null) {
                    logger.warn("Could not acquire file lock for reading");
                    throw new FileOperationException("Could not acquire file lock for reading", null);
                }

                byte[] bytes = Files.readAllBytes(filePath);
                if (bytes.length == 0) {
                    return new ArrayList<>();
                }

                TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {};
                List<User> users = objectMapper.readValue(bytes, typeReference);

                logger.debug("Read {} users from file", users.size());
                return users;

            } catch (IOException e) {
                logger.error("Error reading users file", e);
                throw new FileOperationException("Error reading users file", e);
            }
        } catch (IOException e) {
            logger.error("Error opening file channel for reading", e);
            throw new FileOperationException("Error opening file channel for reading", e);
        }
    }

    private void writeUsersToFile(List<User> users) {
        Path tempPath = Paths.get(TEMP_FILE);
        Path filePath = Paths.get(USERS_FILE);

        try {
            // Write to temporary file first
            String jsonContent = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(users);
            Files.write(tempPath, jsonContent.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

            // Acquire exclusive lock and atomic move
            try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
                try (FileLock lock = channel.tryLock()) {
                    if (lock == null) {
                        Files.delete(tempPath); // Clean up temp file
                        throw new FileOperationException("Could not acquire file lock for writing", null);
                    }

                    // Atomic move from temp to actual file
                    Files.move(tempPath, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                    logger.debug("Successfully wrote {} users to file", users.size());

                } catch (IOException e) {
                    Files.deleteIfExists(tempPath); // Clean up temp file on error
                    logger.error("Error during file write operation", e);
                    throw new FileOperationException("Error during file write operation", e);
                }
            }
        } catch (IOException e) {
            try {
                Files.deleteIfExists(tempPath); // Clean up temp file
            } catch (IOException cleanupException) {
                logger.warn("Failed to clean up temporary file", cleanupException);
            }
            logger.error("Error writing users file", e);
            throw new FileOperationException("Error writing users file", e);
        }
    }
}