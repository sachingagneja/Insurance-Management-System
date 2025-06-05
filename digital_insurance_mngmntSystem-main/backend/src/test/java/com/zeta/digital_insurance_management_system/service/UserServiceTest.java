package com.zeta.digital_insurance_management_system.service;

import com.zeta.digital_insurance_management_system.dto.auth.UserDTO;
import com.zeta.digital_insurance_management_system.enums.Role;
import com.zeta.digital_insurance_management_system.exception.InvalidCredentialsException;
import com.zeta.digital_insurance_management_system.exception.UserAlreadyExistException;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.repository.UserRepository;
import com.zeta.digital_insurance_management_system.security.jwt.JwtService;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setPhone("1234567890");
        testUser.setAddress("123 Main St");
        testUser.setRole(Role.USER);
    }

    @Test
    void register_shouldCreateUserSuccessfully() {
        User inputUser = new User();
        inputUser.setName("John Doe");
        inputUser.setEmail("john.doe@example.com");
        inputUser.setPassword("password123");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.register(inputUser);

        assertNotNull(result);
        assertEquals(Role.USER, inputUser.getRole());
        assertNotNull(inputUser.getPassword());
        assertNotEquals("password123", inputUser.getPassword());

        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUserAlreadyExists() {
        User inputUser = new User();
        inputUser.setEmail("john.doe@example.com");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);

        UserAlreadyExistException exception = assertThrows(
                UserAlreadyExistException.class,
                () -> userService.register(inputUser)
        );

        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        User loginUser = new User();
        loginUser.setEmail("john.doe@example.com");
        loginUser.setPassword("password123");

        Authentication mockAuth = mock(Authentication.class);
        String expectedToken = "mock.jwt.token";

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtService.generateToken(testUser)).thenReturn(expectedToken);

        String result = userService.login(loginUser);

        assertEquals(expectedToken, result);
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(testUser);
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        User loginUser = new User();
        loginUser.setEmail("nonexistent@example.com");
        loginUser.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(loginUser)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_shouldThrowException_whenAuthenticationFails() {
        User loginUser = new User();
        loginUser.setEmail("john.doe@example.com");
        loginUser.setPassword("wrongpassword");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(loginUser)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("jane.doe@example.com");

        List<User> expectedUsers = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<User> result = userService.getAllUsers();

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertEquals(testUser, result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_shouldReturnNull_whenUserNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User result = userService.getUserById(999L);

        assertNull(result);
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getCurrentEmail_shouldReturnEmail_whenUserDetailsAvailable() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("john.doe@example.com");

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(mockUserDetails);

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);

        SecurityContextHolder.setContext(mockSecurityContext);

        String result = userService.getCurrentEmail();

        assertEquals("john.doe@example.com", result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentEmail_shouldReturnNull_whenNoUserDetails() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn("some-string-principal");

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);

        SecurityContextHolder.setContext(mockSecurityContext);

        String result = userService.getCurrentEmail();

        assertNull(result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentEmail_shouldReturnNull_whenNoAuthentication() {
        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(mockSecurityContext);

        assertThrows(NullPointerException.class, () -> {
            userService.getCurrentEmail();
        });

        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_shouldReturnUserId_whenTokenAvailable() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getCredentials()).thenReturn("mock.jwt.token");

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);

        SecurityContextHolder.setContext(mockSecurityContext);

        when(jwtService.extractUserId("mock.jwt.token")).thenReturn(1L);

        Long result = userService.getCurrentUserId();

        assertEquals(1L, result);
        verify(jwtService, times(2)).extractUserId("mock.jwt.token");

        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_shouldFallbackToEmail_whenNoToken() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("john.doe@example.com");

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(mockUserDetails);
        when(mockAuth.getCredentials()).thenReturn(null);

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);

        SecurityContextHolder.setContext(mockSecurityContext);

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);

        Long result = userService.getCurrentUserId();

        assertEquals(1L, result);
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(jwtService, never()).extractUserId(anyString());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_shouldReturnNull_whenNoAuthenticationAndNoEmail() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getCredentials()).thenReturn(null);
        when(mockAuth.getPrincipal()).thenReturn("some-string-principal");

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);

        SecurityContextHolder.setContext(mockSecurityContext);

        Long result = userService.getCurrentUserId();

        assertNull(result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserByEmail_shouldReturnUser_whenUserExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);

        User result = userService.getUserByEmail("john.doe@example.com");

        assertEquals(testUser, result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void getUserByEmail_shouldReturnNull_whenUserNotExists() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        User result = userService.getUserByEmail("nonexistent@example.com");

        assertNull(result);
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void convertToDTO_shouldConvertUserToDTO() {
        UserDTO result = userService.convertToDTO(testUser);

        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getRole(), result.getRole());
    }

    @Test
    void convertToDTO_shouldHandleNullValues() {
        User userWithNulls = new User();
        userWithNulls.setId(1L);
        userWithNulls.setName(null);
        userWithNulls.setEmail("test@example.com");
        userWithNulls.setRole(Role.USER);

        UserDTO result = userService.convertToDTO(userWithNulls);

        assertEquals(1L, result.getId());
        assertNull(result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void register_shouldEncodePassword() {
        User inputUser = new User();
        inputUser.setName("Test User");
        inputUser.setEmail("test@example.com");
        inputUser.setPassword("plainPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.register(inputUser);

        assertTrue(inputUser.getPassword().startsWith("$2"));
        assertNotEquals("plainPassword", inputUser.getPassword());
        assertEquals(Role.USER, inputUser.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }
}