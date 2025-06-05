package com.zeta.digital_insurance_management_system.controller;

import com.zeta.digital_insurance_management_system.dto.auth.AuthResponseDTO;
import com.zeta.digital_insurance_management_system.dto.auth.UserDTO;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestMapping("/auth")
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody User user) {
        logger.info("Received registration request for email: {}", user.getEmail());
        User createdUser = userService.register(user);
        UserDTO userDTO = userService.convertToDTO(createdUser);
        logger.info("User registered successfully with id: {}", createdUser.getId());
        return ResponseEntity.status(201).body(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody User user) {
        logger.info("Received login request for email: {}", user.getEmail());
        String token = userService.login(user);
        User userDetails = userService.getUserByEmail(user.getEmail());
        UserDTO userDTO = userService.convertToDTO(userDetails);
        logger.info("User logged in successfully with id: {}", userDetails.getId());
        return ResponseEntity.ok(new AuthResponseDTO(token, userDTO));
    }
}
