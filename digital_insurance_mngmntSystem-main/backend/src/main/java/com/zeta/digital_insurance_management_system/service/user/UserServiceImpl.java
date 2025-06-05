package com.zeta.digital_insurance_management_system.service.user;

import com.zeta.digital_insurance_management_system.dto.auth.UserDTO;
import com.zeta.digital_insurance_management_system.enums.Role;
import com.zeta.digital_insurance_management_system.exception.InvalidCredentialsException;
import com.zeta.digital_insurance_management_system.exception.UserAlreadyExistException;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.repository.UserRepository;
import com.zeta.digital_insurance_management_system.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(User user) {
        logger.info("Attempting to register user with email: {}", user.getEmail());
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            logger.warn("Registration failed. User already exists with email: {}", user.getEmail());
            throw new UserAlreadyExistException("User already exists");
        }
        user.setRole(Role.USER);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        logger.info("User registered successfully with email: {}", user.getEmail());
        return user;
    }

    public String login(User user) {
        logger.info("Login attempt for email: {}", user.getEmail());
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null) {
            logger.warn("Login failed. No user found with email: {}", user.getEmail());
            throw new InvalidCredentialsException("Invalid credentials");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            logger.info("Login successful for email: {}", user.getEmail());
            return jwtService.generateToken(existingUser);
        } catch (Exception e) {
            logger.error("Login failed due to authentication error for email: {}", user.getEmail());
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long id) {
        logger.info("Fetching user by id: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    public String getCurrentEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            logger.info("Current authenticated user's email: {}", userDetails.getUsername());
            return userDetails.getUsername();
        }
        logger.warn("Unable to extract email from principal: {}", principal);
        return null;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Authentication object: {}", authentication);

        if (authentication != null && authentication.getCredentials() instanceof String token) {
            logger.info("Extracting userId from token");
            Long userId = jwtService.extractUserId(token);
            logger.info("Extracted userId: {}", userId);
            return userId;
        }

        String email = getCurrentEmail();
        if (email != null) {
            User user = getUserByEmail(email);
            Long userId = user != null ? user.getId() : null;
            logger.info("Fetched userId {} using email: {}", userId, email);
            return userId;
        }

        logger.warn("Unable to determine current user id");
        return null;
    }

    public User getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    public UserDTO convertToDTO(User user) {
        logger.info("Converting User entity to DTO for userId: {}", user.getId());
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
