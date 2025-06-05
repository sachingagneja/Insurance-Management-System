package com.zeta.digital_insurance_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.digital_insurance_management_system.dto.auth.UserDTO;
import com.zeta.digital_insurance_management_system.enums.Role;
import com.zeta.digital_insurance_management_system.exception.InvalidCredentialsException;
import com.zeta.digital_insurance_management_system.exception.UserAlreadyExistException;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = {UserController.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setPhone("1234567890");
        testUser.setAddress("123 Main St");
        testUser.setRole(Role.USER);

        testUserDTO = new UserDTO(1L, "John Doe", "john.doe@example.com", Role.USER);
    }

    @Test
    @WithMockUser
    void register_shouldReturnCreatedUser() throws Exception {
        User inputUser = new User();
        inputUser.setName("John Doe");
        inputUser.setEmail("john.doe@example.com");
        inputUser.setPassword("password123");
        inputUser.setPhone("1234567890");
        inputUser.setAddress("123 Main St");

        when(userService.register(any(User.class))).thenReturn(testUser);
        when(userService.convertToDTO(testUser)).thenReturn(testUserDTO);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUserDTO.getId()))
                .andExpect(jsonPath("$.name").value(testUserDTO.getName()))
                .andExpect(jsonPath("$.email").value(testUserDTO.getEmail()))
                .andExpect(jsonPath("$.role").value(testUserDTO.getRole().name()));

        verify(userService, times(1)).register(any(User.class));
        verify(userService, times(1)).convertToDTO(testUser);
    }

    @Test
    @WithMockUser
    void register_shouldHandleUserAlreadyExistsException() throws Exception {
        User inputUser = new User();
        inputUser.setName("John Doe");
        inputUser.setEmail("john.doe@example.com");
        inputUser.setPassword("password123");

        when(userService.register(any(User.class)))
                .thenThrow(new UserAlreadyExistException("User already exists"));

        try {
            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputUser)))
                    .andDo(print());
        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            assert cause instanceof UserAlreadyExistException;
            assert cause.getMessage().equals("User already exists");
        }

        verify(userService, times(1)).register(any(User.class));
    }

    @Test
    @WithMockUser
    void login_shouldReturnTokenAndUserDetails() throws Exception {
        User loginUser = new User();
        loginUser.setEmail("john.doe@example.com");
        loginUser.setPassword("password123");

        String mockToken = "mock.jwt.token";

        when(userService.login(any(User.class))).thenReturn(mockToken);
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userService.convertToDTO(testUser)).thenReturn(testUserDTO);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(mockToken))
                .andExpect(jsonPath("$.user.id").value(testUserDTO.getId()))
                .andExpect(jsonPath("$.user.name").value(testUserDTO.getName()))
                .andExpect(jsonPath("$.user.email").value(testUserDTO.getEmail()))
                .andExpect(jsonPath("$.user.role").value(testUserDTO.getRole().name()));

        verify(userService, times(1)).login(any(User.class));
        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
        verify(userService, times(1)).convertToDTO(testUser);
    }

    @Test
    @WithMockUser
    void login_shouldHandleInvalidCredentialsException() throws Exception {
        User loginUser = new User();
        loginUser.setEmail("john.doe@example.com");
        loginUser.setPassword("wrongpassword");

        when(userService.login(any(User.class)))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        try {
            mockMvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginUser)))
                    .andDo(print());
        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            assert cause instanceof InvalidCredentialsException;
            assert cause.getMessage().equals("Invalid credentials");
        }

        verify(userService, times(1)).login(any(User.class));
    }

    @Test
    @WithMockUser
    void register_shouldSetUserRoleByDefault() throws Exception {
        User inputUser = new User();
        inputUser.setName("Jane Doe");
        inputUser.setEmail("jane.doe@example.com");
        inputUser.setPassword("password123");

        User registeredUser = new User();
        registeredUser.setId(2L);
        registeredUser.setName("Jane Doe");
        registeredUser.setEmail("jane.doe@example.com");
        registeredUser.setRole(Role.USER);

        UserDTO registeredUserDTO = new UserDTO(2L, "Jane Doe", "jane.doe@example.com", Role.USER);

        when(userService.register(any(User.class))).thenReturn(registeredUser);
        when(userService.convertToDTO(registeredUser)).thenReturn(registeredUserDTO);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService, times(1)).register(any(User.class));
    }
}